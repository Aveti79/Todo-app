package com.kodart.todoapp.controller;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//Ta adnotacja nakazuje zbudowanie całej aplikacji na potrzeby testu
//Dodatkowo w parametrze ustawiamy port na RANDOM
//@ActiveProfiles("integration")
//Powyższa adnotacja nie jest jednak dobrym rozwiązaniem, ponieważ nawiązujemy połączenie z mockowym repozytorium,
// a chcemy przetestować faktyczne połączenie z bazą H2, dlatego zostały wprowadzone zmiany w TestConfiguration.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {

    //Wstrzykujemy wartość wylosowanego portu do poniższej zmiennej
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    //Tutaj mamy na celu nawiązanie połączenia z oryginalnym repo, nie mockowym, które pod spodem będzie ("SqlTaskRepository")
    //P.S. Po uruchomieniu testów okazuje się, że jednak potrzebne będzie mockowe repozytorium
    //W tym celu dodajemy nad nazwą klasy adnotacje @ActiveProfiles
    @Autowired
    TaskRepository repository;

    @Test
    void httpGet_returnsAllTheTasks() {
        //given
        //Dodajemy dwa taski do repozytorium na potrzeby testu.
        int initial = repository.findAll().size();
        repository.save(new Task("foo", LocalDateTime.now()));
        repository.save(new Task("bar", LocalDateTime.now()));
        //when
        //Dzięki tej metodzie uzyskujemy z zapytania get, pod podanym adresem obiekt Task.class
        //restTemplate.getForEntity("http://localhost:" + port + "/tasks", Task.class);
        //Jednak w tym przypadku potrzebowalibyśmy listy tasków, dlatego modyfikujemy delikatnie powyższą metodę.
        Task[] result = restTemplate.getForObject("http://localhost:" + port + "/tasks", Task[].class);
        //then
        assertThat(result).hasSize(initial+2);
    }


    //Test sprawdzający jeden task
    @Test
    void httpGet_returnsSpecifiedTask() {
        //given
        int id = repository.save(new Task("foo", LocalDateTime.now())).getId();
        //when
        Task result = restTemplate.getForObject("http://localhost:" + port + "/tasks/" + id, Task.class);
        //then
        assertThat(repository.existsById(id)).isTrue();
        assertThat(result.getId()).isEqualTo(repository.findById(id).get().getId());
    }
}