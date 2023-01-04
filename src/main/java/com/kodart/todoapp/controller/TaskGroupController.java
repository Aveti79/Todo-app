package com.kodart.todoapp.controller;

import com.kodart.todoapp.logic.TaskGroupService;
import com.kodart.todoapp.model.Task;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/groups")
public class TaskGroupController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository repository;
    private final TaskGroupService service;

    public TaskGroupController(final TaskRepository repository, final TaskGroupService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model) {
        model.addAttribute("group", new GroupWriteModel());
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
        model.addAttribute("groups", getGroups());
        model.addAttribute("message", "Utworzono grupę!");
        return "groups";
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups() {
        return service.readAll();
    }

    @ModelAttribute("sortTasks")
    List<GroupTaskWriteModel> sortTasksInGroupByDeadline(GroupWriteModel current) {
        return current.getTasks().stream().sorted(
                Comparator.comparing(GroupTaskWriteModel::getDeadline,
                    Comparator.nullsFirst(
                        Comparator.naturalOrder())))
                .collect(Collectors.toList());
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
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> postGroup(@RequestBody @Valid GroupWriteModel toPost) {
        GroupReadModel result = service.createGroup(toPost);
        return ResponseEntity.created(URI.create("/"+result.getId())).body(result);
    }

    @ResponseBody
    @Transactional
    @PatchMapping(path = "/{id}", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<?> updateTaskGroup(@PathVariable int id) {
        service.toggleTaskGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.notFound().build();
    }

    @ResponseBody
    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

}
