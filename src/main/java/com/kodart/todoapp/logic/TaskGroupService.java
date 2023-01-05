package com.kodart.todoapp.logic;

import com.kodart.todoapp.model.Project;
import com.kodart.todoapp.model.TaskGroup;
import com.kodart.todoapp.model.TaskGroupRepository;
import com.kodart.todoapp.model.TaskRepository;
import com.kodart.todoapp.model.projection.GroupReadModel;
import com.kodart.todoapp.model.projection.GroupWriteModel;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TaskGroupService {
    private TaskGroupRepository taskGroupRepository;
    private TaskRepository taskRepository;

    TaskGroupService(final TaskGroupRepository taskGroupRepository, final TaskRepository taskRepository) {
        this.taskGroupRepository = taskGroupRepository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source) {
        return createGroup(source, null);
    }

    GroupReadModel createGroup(final GroupWriteModel source, final Project project) {
        TaskGroup result = taskGroupRepository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return taskGroupRepository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public List<GroupReadModel> readAndSortTasksGroupsByDeadline() {
        return readAll().stream().sorted(
                Comparator.comparing(
                        GroupReadModel::getDeadline, Comparator.nullsFirst(
                                Comparator.naturalOrder()))).
                collect(Collectors.toList());
    }

    public void toggleTaskGroup(int groupId) {
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)) {
            throw new IllegalStateException("You need to get done all tasks in group before group closing.");
        }
        TaskGroup result = taskGroupRepository.findById(groupId).orElseThrow(
                () -> new IllegalArgumentException("TaskGroup with given Id doesn't exists."));
        result.setDone(!result.isDone());
        taskGroupRepository.save(result);
    }
}
