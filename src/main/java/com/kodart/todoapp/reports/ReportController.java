package com.kodart.todoapp.reports;

import com.kodart.todoapp.controller.IllegalExceptionsProcessing;
import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import com.kodart.todoapp.model.event.TaskDone;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@RestController
@IllegalExceptionsProcessing
@RequestMapping("/reports")
public class ReportController {

    private final TaskRepository taskRepository;
    private final PersistedTaskEventRepository eventRepository;

    public ReportController(final TaskRepository taskRepository, final PersistedTaskEventRepository eventRepository) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/count/{id}")
    ResponseEntity<TaskWithChangesCount> readTaskWithCount(@PathVariable int id) {
        return taskRepository.findById(id)
                .map(task -> new TaskWithChangesCount(task, eventRepository.findByTaskId(id)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/done-before-dd/{id}")
    ResponseEntity<TaskDoneBeforeDeadline> checkTaskDoneTime(@PathVariable int id) {
        return taskRepository.findById(id)
                .map(task -> new TaskDoneBeforeDeadline(task, eventRepository.findByTaskId(id)))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new IllegalStateException("There's no changes records for this task Id."));
    }

    private static class TaskWithChangesCount {

        public String description;
        public boolean done;
        public int changesCount;

        TaskWithChangesCount(final Task task, final List<PersistedTaskEvent> events) {
            description = task.getDescription();
            done = task.isDone();
            changesCount = events.size();
        }
    }


    private static class TaskDoneBeforeDeadline {

        public LocalDateTime lastChangeTime;
        public LocalDateTime deadline;
        public boolean isDoneBeforeDeadline;

        public TaskDoneBeforeDeadline(final Task task, final List<PersistedTaskEvent> events) {
            if (task.isDone() && task.getDeadline() != null) {
                lastChangeTime = events.stream()
                        .filter(persistedTaskEvent -> persistedTaskEvent.name.equals(TaskDone.class.getSimpleName()))
                        .map(persistedTaskEvent -> persistedTaskEvent.occurrence).max(Comparator.naturalOrder())
                        .orElseThrow(() -> new IllegalStateException("There wasn't any changes before"));

                deadline = task.getDeadline();
                isDoneBeforeDeadline = lastChangeTime.isBefore(deadline);
            }
            else this.isDoneBeforeDeadline = task.isDone();
        }
    }
}
