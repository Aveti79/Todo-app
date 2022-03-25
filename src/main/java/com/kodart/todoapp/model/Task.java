package com.kodart.todoapp.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends BaseTask {

    private LocalDateTime deadline;

    @Embedded
    private Audit audit = new Audit();
    //Poniższe adnotacje, sprawiają, że zapisujemy wszystkie taski na podstawie columny "task_group_id" do jednej grupy
    @ManyToOne
    @JoinColumn(name = "task_group_id")
    private TaskGroup group;

    public Task() {
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
