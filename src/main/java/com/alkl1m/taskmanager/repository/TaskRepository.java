package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.dto.task.TaskDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Status;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
                SELECT 
                new com.alkl1m.taskmanager.dto.task.TaskDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.deadline, b.status, b.tags)
                FROM Task b
                WHERE b.user = :user and  b.project = :project
            """)
    ArrayList<TaskDto> getAllTasks(@Param("user") User user, @Param("project") Project project);
    Task getTaskById(Long id);
    List<Task> findTop50ByUserIdOrderByDeadlineAsc(Long userId);

    int countByProjectIdAndStatus(Long id, Status status);

    int countByProjectId(Long id);
}
