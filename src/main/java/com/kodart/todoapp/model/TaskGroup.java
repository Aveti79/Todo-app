package com.kodart.todoapp.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "tasks_groups")
public class TaskGroup extends BaseTask {

    @Embedded
    private Audit audit = new Audit();

    // Poniższa adnotacja sprawia, że pobieramy wszystkie taski z grupy i mapujemy je na obiekty Task w kolekcji,
    // Właściwość "cascade" odpowiada, za wykonanie operacji na obiektach poniżej,
    // a mappedBy infrotmuje Springa, że Taski w środku zostały zmapowane według columny "group"
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private Set<Task> tasks;
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    public TaskGroup() {
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(final Set<Task> tasks) {
        this.tasks = tasks;
    }

    Project getProject() {
        return project;
    }

    void setProject(final Project project) {
        this.project = project;
    }

    public void updateFrom(TaskGroup source) {
        super.updateFrom(source);
        this.tasks = source.tasks;
        this.project = source.project;
    }
}
