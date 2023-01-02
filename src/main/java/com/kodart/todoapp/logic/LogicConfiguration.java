package com.kodart.todoapp.logic;

import com.kodart.todoapp.TaskConfigurationProperties;
import com.kodart.todoapp.model.ProjectRepository;
import com.kodart.todoapp.model.TaskGroupRepository;
import com.kodart.todoapp.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Klasa konfiguracyjna, która w alternatywny sposób dodaje komponenty do kontekstu Springa.
 * Daje to trochę większą niezależnośc klas zawierających logikę od Springa,
 * ponieważ teraz nie stosujemy już adnotacji w poszczególnych klasach,
 * a całość mieści się w tej klasie konfiguracyjnej.
 */
@Configuration
public class LogicConfiguration {
    @Bean
    ProjectService projectService(final ProjectRepository projectRepository,
                                  final TaskGroupRepository taskGroupRepository,
                                  final TaskGroupService taskGroupService,
                                  final TaskConfigurationProperties taskConfigurationProperties) {
        return new ProjectService(projectRepository, taskGroupRepository, taskGroupService, taskConfigurationProperties);
    }

    @Bean
    TaskGroupService taskGroupService(final TaskGroupRepository taskGroupRepository,
                                      final TaskRepository taskRepository) {
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }

    @Bean
    TaskService taskService(final TaskRepository repository) {
        return new TaskService(repository);
    }
}
