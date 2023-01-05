package com.kodart.todoapp.model;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    List<Project> findAll();

    Optional<Project> findById(Integer id);

    Project save (Project entity);

    void delete (Project entity);
}
