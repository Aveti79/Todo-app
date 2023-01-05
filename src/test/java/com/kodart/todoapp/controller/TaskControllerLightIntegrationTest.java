package com.kodart.todoapp.controller;

import com.kodart.todoapp.logic.TaskService;
import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(TaskController.class)
public class TaskControllerLightIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository repository;

    @MockBean
    private TaskService service;

    @Test
    void httpGet_returnGivenTask() throws Exception {
        //given
        String description = "foo";
        when(repository.findById(anyInt())).thenReturn(Optional.of(new Task("foo", LocalDateTime.now())));
        //expect
        mockMvc.perform(get("/tasks/" + 123))
                .andDo(print())
                .andExpect(content().string(containsString(description)));
    }
}
