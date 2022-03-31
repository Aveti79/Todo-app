package com.kodart.todoapp.logic;

import com.kodart.todoapp.model.TaskGroup;
import com.kodart.todoapp.model.TaskGroupRepository;
import com.kodart.todoapp.model.TaskRepository;
import com.kodart.todoapp.model.projection.GroupReadModel;
import com.kodart.todoapp.model.projection.GroupWirteModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskGroupService {
    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    TaskGroupService(final TaskGroupRepository repository) {
        this.repository = repository;
    }

    public GroupReadModel createGroup(GroupWirteModel source) {
        TaskGroup result = repository.save(source.toGroup());
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll() {
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleTaskGroup(int groupId) {
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)) {
            throw new IllegalStateException("You need to get done all tasks in group before group closing.");
        }
        TaskGroup result = repository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("TaskGroup with given Id doesn't exists."));
        result.setDone(!result.isDone());
    }
}
