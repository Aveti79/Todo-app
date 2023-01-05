package com.kodart.todoapp;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
public class TestConfiguration {

    //Dodajemy testową bazę danych, która będzie wykorzystywana w testach na profilu innym niż "integration"
    @Bean
    @Primary
    @Profile("!integration")
    DataSource e2eTestDataSource() {
        var result = new DriverManagerDataSource("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        result.setDriverClassName("org.h2.Driver");
        return result;
    }

    @Bean
    @Primary
    @Profile("integration")
    TaskRepository testRepo() {
        return new TaskRepository() {
            private final Map<Integer, Task> tasks = new HashMap<>();

            @Override
            public List<Task> findAll() {
                return new ArrayList<>(tasks.values());
            }

            @Override
            public Page<Task> findAll(final Pageable page) {
                return null;
            }

            @Override
            public Optional<Task> findById(final Integer id) {
                return Optional.ofNullable(tasks.get(id));
            }

            @Override
            public List<Task> findAllByGroup_Id(final Integer groupId) {
                return List.of();
            }

            @Override
            public List<Task> findAllByDeadline(final LocalDateTime today) {
                return null;
            }

            @Override
            public boolean existsById(final Integer id) {
                return tasks.containsKey(id);
            }

            @Override
            public boolean existsByDoneIsFalseAndGroup_Id(final Integer groupId) {
                return false;
            }

            @Override
            public void delete(final Task entity) {

            }

            @Override
            public Task save(final Task entity) {
                int key = tasks.size() + 1;
                try {
                    var field = Task.class.getSuperclass().getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, key);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                tasks.put(key, entity);
                return tasks.get(key);
            }
        };
    }
}

