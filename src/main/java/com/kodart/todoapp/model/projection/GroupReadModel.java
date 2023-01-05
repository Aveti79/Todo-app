package com.kodart.todoapp.model.projection;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskGroup;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GroupReadModel {

    private int id;
    private String description;
    private boolean done;
    /**
     * Deadline from the latest task in group
     */
    private LocalDateTime deadline;
    private List<GroupTaskReadModel> tasks;

    public GroupReadModel(TaskGroup source) {
        id = source.getId();
        description = source.getDescription();
        source.getTasks().stream()
                .map(Task::getDeadline)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .ifPresent(date -> deadline = date);
        done = source.isDone();
        tasks = getTasksSortedByDeadline(source);
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(final boolean done) {
        this.done = done;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public List<GroupTaskReadModel> getTasks() {
        return tasks;
    }

    public void setTasks(final List<GroupTaskReadModel> tasks) {
        this.tasks = tasks;
    }

    public List<GroupTaskReadModel> getTasksSortedByDeadline(TaskGroup source) {
        return source.getTasks().stream().sorted(
                Comparator.comparing(Task::getDeadline,
                        Comparator.nullsFirst(
                                Comparator.naturalOrder()))).
                map(GroupTaskReadModel::new).collect(Collectors.toList());
    }
}
