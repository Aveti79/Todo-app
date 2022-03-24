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

    // Poniższa adnotacja sprawia, że pobieramy wszystkie taski z grupy i mapujemy je na obiekty Task w kolekcji,
    // Właściwość "cascade" odpowiada, za wykonanie operacji na obiektach poniżej,
    // a mappedBy infrotmuje Springa, że Taski w środku zostały zmapowane według columny "group"
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
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
