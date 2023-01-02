package com.kodart.todoapp.logic;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;

    public TaskService(final TaskRepository repository) {
        this.repository = repository;
    }

    /**
     * Odczytanie pełnej listy tasków, może potencjalnie zając więcej czasu,
     * dlatego w tym przypadku została zastosowana asynchronczność.
     */

    @Async
    public CompletableFuture<List<Task>> findAllAsync() {
        logger.info("Supply async");
        return CompletableFuture.supplyAsync(this::getTaskList);
    }

    private List<Task> getTaskList() {
        List<Task> tasks = repository.findAll()
                .stream()
                .sorted(Comparator.comparing(Task::getDeadline,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        return tasks;
    }
}
