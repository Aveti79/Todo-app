package com.kodart.todoapp.logic;

import com.kodart.todoapp.TaskConfigurationProperties;
import com.kodart.todoapp.model.*;
import com.kodart.todoapp.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    /**
     * 1. Tworzę mockowe repozytoria przy pomocy Mockito, abym mógł utworzyć obiekt ProjectSerivce z danymi warunkami
     */
    @Test
    @DisplayName("Should throw IllegalStateException when configure to allow just 1 group and the other undone group exists")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throws_IllegalStateException() {

        //given//
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        //System under test (SUT)
        var toTest = new ProjectService(null, mockGroupRepository, null, mockConfig);

        //when//
        var exception = catchThrowable(() -> toTest.createGroup(1, LocalDateTime.now()));

        //then//
        assertThat(exception).isInstanceOf(IllegalStateException.class).hasMessageContaining("one undone group");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when configuration is ok but there is no project with given id")
    void createGroup_withConfigurationOk_And_noProjects_throws_IllegalArgumentException() {

        //given//
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //System under test (SUT)
        var toTest = new ProjectService(mockRepository, null, null, mockConfig);

        //when//
        var exception = catchThrowable(() -> toTest.createGroup(1, LocalDateTime.now()));

        //then//
        assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ID not found");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when configured to allow just 1 group and there is no groups and project with given id")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroupExists_noProject_throws_IllegalArgumentException() {

        //given//
        var mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        //and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        //System under test (SUT)
        var toTest = new ProjectService(mockRepository, mockGroupRepository, null, mockConfig);

        //when//
        var exception = catchThrowable(() -> toTest.createGroup(1, LocalDateTime.now()));

        //then//
        assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("ID not found");
    }

    @Test
    @DisplayName("Should create a now group from project")
    void createGroup_configurationOk_existingProject_createsAndSavesGroup() {
        // given
        var today = LocalDate.now().atStartOfDay();
        // and
            //Tworzę projekt z opisem bar i dwoma taskami z deadlinem -1 i -2, oraz opsiem "foo"
        var project = projectWith("bar", Set.of(-1, -2));
            //Tworzę mockowe repozytorium ProjectRepository
        var mockRepository = mock(ProjectRepository.class);
            //Kiedy ktoś zawoła na mockowym repozytorium metodę findById, zwracam Optionala od projektu zapisanego w repozytorium
        when(mockRepository.findById(anyInt())).thenReturn(Optional.of(project));
        // and
        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        int countBeforeCall = inMemoryGroupRepo.count();
        var taskGroupService = new TaskGroupService(inMemoryGroupRepo, null);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // System under test
        var toTest = new ProjectService(mockRepository, inMemoryGroupRepo, taskGroupService, mockConfig);
        // when
            //Tworzę grupę z projektu wołając na mockowym ProjectRepository metodę findById;
        GroupReadModel result = toTest.createGroup(1, today);
            //Niestety w obiekcie result opis wynosi "null", czyli został po drodze zgubiony.
        // then
            //Następnie ten test się wywala bo "null" =/= "bar"
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks().stream().allMatch(task -> task.getDescription().equals("foo")));
        assertThat(countBeforeCall + 1).isEqualTo(inMemoryGroupRepo.count());
    }

    private Project projectWith(String description, Set<Integer> daysToDeadline) {
        Set<ProjectStep> mockProjectSteps = daysToDeadline.stream()
                .map(days -> {
                    var step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("foo");
                    when(step.getDaysToDeadline()).thenReturn(days);
                    return step;
                }).collect(Collectors.toSet());
        var result = mock(Project.class);
        when(result.getDescription()).thenReturn(description);
        when(result.getSteps()).thenReturn(mockProjectSteps);
        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(final boolean t) {
        var mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(t);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(final boolean t) {
        var mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(t);
        //and
        var mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);
        return mockConfig;
    }

    private InMemoryGroupRepository inMemoryGroupRepository() {
        return new InMemoryGroupRepository();
    }

    private static class InMemoryGroupRepository implements TaskGroupRepository {
        private int index = 0;
        private final Map<Integer, TaskGroup> map = new HashMap<>();

        public int count() {
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(final Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(final TaskGroup entity) {
            /**
             * Poniższy sposób nazywany jest nadpisaniem przez refleksje.
             * Jego zadaniem jest nadpisanie niepublicznego pola ID
             * znajdującego się w klasie TaskGroup, dla obiektu entity.
             */
            if (entity.getId() == 0) {
                try {
                    var field = TaskGroup.class.getSuperclass().getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException();
                }
            }
            map.put(++index, entity);
            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(final Integer projectId) {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null
                            && group.getProject().getId() == projectId);
        }
    }
}