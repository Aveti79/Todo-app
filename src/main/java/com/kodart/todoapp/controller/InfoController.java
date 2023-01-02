package com.kodart.todoapp.controller;

import com.kodart.todoapp.TaskConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/info")
public class InfoController {

    // Poniższa zmienna otrzymuje wartość ponieważ jest typu DataSourceProperties,
    // które posiada adnotację @ConfigurationProperties, a adnotacja @Autowired
    // wstrzykuje do zmiennej dataSource obiekt DataSourceProperties i udostępnia metody z tej klasy.
    private final DataSourceProperties dataSource;
    private final TaskConfigurationProperties myProp;

    InfoController(final DataSourceProperties dataSource, final TaskConfigurationProperties myProp) {
        this.dataSource = dataSource;
        this.myProp = myProp;
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/url")
    String url() {
        return dataSource.getUrl();
    }

    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    @GetMapping("/prop")
    boolean myProp() {
        return myProp.getTemplate().isAllowMultipleTasks();
    }
}
