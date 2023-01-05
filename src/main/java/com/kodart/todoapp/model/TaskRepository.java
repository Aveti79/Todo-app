package com.kodart.todoapp.model;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();

    Page<Task> findAll(Pageable page);

    Optional<Task> findById(Integer id);

    List<Task> findAllByGroup_Id(Integer groupId);

    List<Task> findAllByDeadline(LocalDateTime today);

    boolean existsById(Integer id);

    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);

    void delete(Task entity);

    Task save(Task entity);
}
