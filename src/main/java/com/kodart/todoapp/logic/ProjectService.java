package com.kodart.todoapp.logic;

import com.kodart.todoapp.TaskConfigurationProperties;
import com.kodart.todoapp.model.*;
import com.kodart.todoapp.model.projection.GroupReadModel;
import com.kodart.todoapp.model.projection.GroupTaskWriteModel;
import com.kodart.todoapp.model.projection.GroupWriteModel;
import com.kodart.todoapp.model.projection.ProjectWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectService {

    private final ProjectRepository repository;
    private final TaskGroupRepository taskRepository;
    private final TaskGroupService taskGroupService;
    private final TaskConfigurationProperties config;

    ProjectService(final ProjectRepository repository, final TaskGroupRepository taskRepository, final TaskGroupService taskGroupService, final TaskConfigurationProperties config) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.taskGroupService = taskGroupService;
        this.config = config;
    }

    public List<Project> readAll() {
        return repository.findAll();
    }

    public Project save(ProjectWriteModel toSave) {
        return repository.save(toSave.toProject());
    }

    /**
     * This method is creating TaskGroup with Tasks from Project schema
     *
     * @param projectId
     * @param deadline
     * @return
     */
    public GroupReadModel createGroup(int projectId, LocalDateTime deadline) {
        if (!config.getTemplate().isAllowMultipleTasks() && taskRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }

        return repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(projectStep -> {
                                                var task = new GroupTaskWriteModel();
                                                task.setDescription(projectStep.getDescription());
                                                task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                                return task;
                                            }
                                    ).collect(Collectors.toList())
                    );
                    return taskGroupService.createGroup(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given ID not found."));
    }
    /*
    public GroupReadModel createGroup(int projectId, LocalDateTime deadline) {
        //Sprawdzamy, czy podstawowe warunki do stworzenia grupy z projektu zostały spełnione
        if (!config.getTemplate().isAllowMultipleTasks() && taskRepository.existsByDoneIsFalseAndProject_Id(projectId)) {
            throw new IllegalStateException("Only one undone group from project is allowed");
        }

        //Jeżeli poprzednio nie polecał wyjątek, przechodzę do tworzenia grupy
        TaskGroup result = repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new TaskGroup();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                            .map(projectStep -> new Task(
                                        projectStep.getDescription(),
                                        deadline.plusDays(projectStep.getDaysToDeadline()))
                            ).collect(Collectors.toSet())
                    );
                    return targetGroup;
                }).orElseThrow(() -> new IllegalArgumentException("Project with given ID not found."));
        taskRepository.save(result);
        return new GroupReadModel(result);
    }*/
}