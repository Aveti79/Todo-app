package com.kodart.todoapp.controller;

import com.kodart.todoapp.logic.TaskService;
import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequestMapping("/tasks")
class TaskController {
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository repository;
    private final TaskService service;

    TaskController(final ApplicationEventPublisher eventPublisher, final TaskRepository repository, final TaskService service) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String readAllTasks() {
        return "tasks";
    }

    @ResponseBody
    @GetMapping(path = "/json", produces = MediaType.APPLICATION_JSON_VALUE)
    CompletableFuture<ResponseEntity<List<Task>>> getTaskListAsync() {
        return service.findAllAsync().thenApply(ResponseEntity::ok);
    }

    @ResponseBody
    @GetMapping("/{id}")
    ResponseEntity<Task> readTask(@PathVariable int id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ResponseBody
    @GetMapping("/search/today")
    ResponseEntity<List<Task>> readTodayTasks() {
        var today = LocalDateTime.of(
                LocalDate.now().getYear(),
                LocalDate.now().getMonth(),
                LocalDate.now().getDayOfMonth(),
                23,59,59,999);
        return ResponseEntity.ok(repository.findAllByDeadline(today));
    }

    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Task> postTask(@RequestBody @Valid Task toPost) {
        Task result = repository.save(toPost);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.getId())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }

    @ResponseBody
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteTask(@PathVariable int id) {
        repository.findById(id).ifPresent(repository::delete);

        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @PutMapping(path = "/edit/{id}")
    ResponseEntity<?> editTask(@PathVariable int id, @RequestBody @Valid Task toUpdate) {
        repository.findById(id)
                .ifPresent(task -> {
                    toUpdate.setGroup(task.getGroup());
                    task.updateFrom(toUpdate);
                    repository.save(task);
                });
        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.findById(id)
                .map(Task::toggle)
                .ifPresent(eventPublisher::publishEvent);
        return ResponseEntity.noContent().build();
    }

    @ModelAttribute("tasks")
    CompletableFuture<List<Task>> getTaskGroups() {
        return service.findAllAsync();
    }
}
