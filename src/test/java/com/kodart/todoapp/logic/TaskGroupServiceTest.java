package com.kodart.todoapp.logic;

import com.kodart.todoapp.model.TaskGroup;
import com.kodart.todoapp.model.TaskGroupRepository;
import com.kodart.todoapp.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {

    @Test
    @DisplayName("Should throw IllegalStateException when undone group with given group id exists")
    void toggleGroup_undoneGroupExists_with_givenId_throwsIllegalStateException() {
        //given
        var mockTaskRepository = getMockTaskRepository(true);
        var mockTaskGroupRepo = mock(TaskGroupRepository.class);
        //SystemUnderTest
        var toTest = new TaskGroupService(mockTaskGroupRepo, mockTaskRepository);
        //when
        var exception = catchThrowable(() -> toTest.toggleTaskGroup(1));
        //then
        assertThat(exception).isInstanceOf(IllegalStateException.class).hasMessageContaining("get done all tasks");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when group with given id doesn't exist")
    void toggleGroup_groupWithGivenIdDoesntExist_throwsIllegalArgumentException() {
        //given
        var mockTaskRepository = getMockTaskRepository(false);
        var mockTaskGroupRepo = mock(TaskGroupRepository.class);
        //SystemUnderTest
        var toTest = new TaskGroupService(mockTaskGroupRepo, mockTaskRepository);
        //when
        var exception = catchThrowable(() -> toTest.toggleTaskGroup(1));
        //then
        assertThat(exception).isInstanceOf(IllegalArgumentException.class).hasMessageContaining("Id doesn't exists");
    }

    @Test
    @DisplayName("Should change done statement of group with givenId")
    void toggleGroup_groupWithGivenIdExists_And_noUndoneGroups() {
        //given
        var taskGroup = new TaskGroup();
        var beforeToggle = taskGroup.isDone();
        //and
        var mockTaskRepository = getMockTaskRepository(false);
        var mockTaskGroupRepo = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepo.findById(anyInt())).thenReturn(Optional.of(taskGroup));
        //SystemUnderTest
        var toTest = new TaskGroupService(mockTaskGroupRepo, mockTaskRepository);
        //when
        toTest.toggleTaskGroup(1);
        //then
        assertThat(taskGroup.isDone()).isNotEqualTo(beforeToggle);
    }

    private TaskRepository getMockTaskRepository(boolean t) {
        var mockRepo = mock(TaskRepository.class);
        when(mockRepo.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(t);
        return mockRepo;
    }

}