package com.kodart.todoapp.model;

import com.kodart.todoapp.model.event.TaskEvent;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;

@MappedSuperclass
abstract class BaseTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Task's description can't be null.")
    private String description;
    private boolean done;

    public int getId() {
        return id;
    }

    void setId(final int id) {
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

    public TaskEvent toggle() {
        this.done = !this.done;
        return TaskEvent.changed((Task) this);
    }

    public void updateFrom (BaseTask source) {
        description = source.description;
        done = source.done;
    }
}
