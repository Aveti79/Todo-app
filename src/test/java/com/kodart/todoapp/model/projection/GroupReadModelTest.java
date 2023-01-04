package com.kodart.todoapp.model.projection;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class GroupReadModelTest {
    @Test
    @DisplayName("Should create null deadline for group when no task deadlines")
    void constructor_noDeadlines_createsNullDeadline() {
        //given
        var source = new TaskGroup();
        source.setDescription("foo");
        source.setTasks(Set.of(new Task("bar", null)));
        //when
        var result = new GroupReadModel(source);
        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline",null);
    }
}