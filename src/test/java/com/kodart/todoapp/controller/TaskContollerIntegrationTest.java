package com.kodart.todoapp.controller;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class TaskContollerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @Test
    void httpGet_returnGivenTask() throws Exception {
        //given
        int id = repository.save(new Task("foo", LocalDateTime.now())).getId();
        //expect
        mockMvc.perform(get("/tasks/" + id)).andExpect(status().is2xxSuccessful());
    }

    //Test pobierający listę tasków
    @Test
    void httpGet_returnTasksList() throws Exception {
        var resultBefore = mockMvc.perform(get("/tasks/"));
        repository.save(new Task("foo", LocalDateTime.now()));
        repository.save(new Task("bar", LocalDateTime.now()));
        mockMvc.perform(get("/tasks/")).andExpect(status().is2xxSuccessful());
    }

    //Test sprawdzający zapisa Taska
    @Test
    void httpPost_returnLocation() throws Exception {
        String requestBody = "{\n" +
                "    \"done\" : true,\n" +
                "    \"description\" : \"Test with Postman\"\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/tasks/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)).andExpect(status().isCreated());
    }
}
