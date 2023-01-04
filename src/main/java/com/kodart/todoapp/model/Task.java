package com.kodart.todoapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends BaseTask {

    private LocalDateTime deadline;

    @Embedded
    private Audit audit = new Audit();
    //Poniższe adnotacje, sprawiają, że zapisujemy wszystkie taski na podstawie columny "task_group_id" do jednej grupy
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;

    public Task() {
    }

    public Task(String description, LocalDateTime deadline) {
        this(description, deadline, null);
    }

    public Task(String description, LocalDateTime deadline, TaskGroup group) {
        setDescription(description);
        this.deadline = deadline;
        if (group!=null) {
            this.group = group;
        }
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    void setDeadline(final LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public TaskGroup getGroup() {
        return group;
    }

    void setGroup(final TaskGroup group) {
        this.group = group;
    }

    public void updateFrom (Task source) {
        super.updateFrom(source);
        deadline = source.deadline;
        group = source.group;
    }

}
