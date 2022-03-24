package com.kodart.todoapp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(name = "tasks_groups")
public class TaskGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Task's description can't be null.")
    private String description;
    private boolean done;

    @Embedded
    private Audit audit = new Audit();
    // Poniższa adnotacja sprawia, odczytujemy całą kolekcję tasków, tylko w momencie jej wywołania
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private Set<Task> tasks;


    public TaskGroup() {
    }

    public int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    Set<Task> getTasks() {
        return tasks;
    }

    void setTasks(final Set<Task> tasks) {
        this.tasks = tasks;
    }
}
