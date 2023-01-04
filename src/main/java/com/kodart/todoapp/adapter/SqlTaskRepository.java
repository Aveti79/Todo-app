package com.kodart.todoapp.adapter;

import com.kodart.todoapp.model.Task;
import com.kodart.todoapp.model.TaskRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
interface SqlTaskRepository extends TaskRepository, JpaRepository<Task, Integer> {

    @Override
    List<Task> findAllByGroup_Id(Integer groupId);

    @Override
    @Query("select t from Task t where t.deadline is null or t.deadline <:deadline and t.done = false")
    List<Task> findAllByDeadline(@Param("deadline") LocalDateTime today);

    @Override
    @Query(nativeQuery = true, value = "select count(*) > 0 from tasks where id=:id")
    boolean existsById(@Param("id") Integer id);

    @Override
    boolean existsByDoneIsFalseAndGroup_Id(Integer groupId);
}
