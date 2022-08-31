package com.kodart.todoapp.reports;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersistedTaskEventRepository extends JpaRepository<PersistedTaskEvent, Integer> {

    List<PersistedTaskEvent> findByTaskId(int taskId);

}
