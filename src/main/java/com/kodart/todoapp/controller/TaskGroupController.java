package com.kodart.todoapp.controller;

import com.kodart.todoapp.logic.TaskGroupService;
import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskGroup;
import com.kodart.todoapp.model.TaskGroupRepository;
import com.kodart.todoapp.model.TaskRepository;
import com.kodart.todoapp.model.projection.GroupReadModel;
import com.kodart.todoapp.model.projection.GroupTaskWriteModel;
import com.kodart.todoapp.model.projection.GroupWriteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class TaskGroupController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;
    private final TaskGroupService service;
    private final TaskGroupRepository taskGroupRepository;

    public TaskGroupController(final TaskRepository repository, final TaskGroupService service, final TaskGroupRepository taskGroupRepository) {
        this.repository = repository;
        this.service = service;
        this.taskGroupRepository = taskGroupRepository;
    }

    /**
     * String showGroups(@ModelAttribute("group") GroupWriteModel groupWriteModel)
     * is the same as
     * String showGroups(Model model) {
     * model.addAttribute("group", new GroupWriteModel());
     * }
     */
    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model) {
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", service.readAndSortTasksGroupsByDeadline());
        return "groups";
    }

    @PostMapping(params = "addTask", produces = MediaType.TEXT_HTML_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current) {
        current.getTasks().add(new GroupTaskWriteModel());
        return "groups";
    }

    @PostMapping(params = "removeTask", produces = MediaType.TEXT_HTML_VALUE)
    String removeGroupTask(@ModelAttribute("group") GroupWriteModel current, @RequestParam(value = "removeTask") int taskIndex) {
        current.getTasks().remove(taskIndex);
        return "groups";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.TEXT_HTML_VALUE)
    String addGroup(@ModelAttribute("group") @Valid GroupWriteModel current,
                    BindingResult result,
                    Model model) {
        if (result.hasErrors()) {
            return "groups";
        }
        service.createGroup(current);
        model.addAttribute("group", new GroupWriteModel());
        showGroups(model);
        model.addAttribute("message", "Utworzono grupę!");
        return "groups";
    }

    @DeleteMapping("/{id}")
    String deleteGroup (@PathVariable int id, Model model) {
        service.deleteTaskGroup(id);
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups", getTaskGroups());
        return "groups";
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getTaskGroups() {
        return service.readAndSortTasksGroupsByDeadline();
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupReadModel>> readAllGroups() {
        logger.info("Exposing all task groups!");
        return ResponseEntity.ok(service.readAll());
    }

    @ResponseBody
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksInGroup(@PathVariable final int id) {
        List<Task> result = repository.findAllByGroup_Id(id);
        return ResponseEntity.ok(result);
    }

    @ResponseBody
    @PostMapping(path = "/{id}/tasks", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> addTaskToGroup(@RequestBody Task toPost, @PathVariable int id) {
        taskGroupRepository.findById(id).ifPresent(taskGroup -> {
            toPost.setGroup(taskGroup);
            taskGroup.getTasks().add(toPost);
            taskGroupRepository.save(taskGroup);
        });
        return ResponseEntity.created(URI.create("/groups/" + id)).body(taskGroupRepository.findById(id));
    }

    @ResponseBody
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> postGroup(@RequestBody @Valid GroupWriteModel toPost) {
        GroupReadModel result = service.createGroup(toPost);
        return ResponseEntity.created(URI.create("/" + result.getId())).body(result);
    }

    @ResponseBody
    @PutMapping(path = "/edit/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> editTaskGroup(@PathVariable int id, @RequestBody @Valid TaskGroup toUpdate) {
        taskGroupRepository.findById(id)
                .ifPresent(taskGroup -> {
                    taskGroup.updateFrom(toUpdate);
                    taskGroupRepository.save(taskGroup);
                });
        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @Transactional
    @PatchMapping(path = "/{id}")
    ResponseEntity<?> updateTaskGroup(@PathVariable int id) {
        service.toggleTaskGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        logger.info(e.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<?> handleIllegalState(IllegalStateException e) {
        logger.info(e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
