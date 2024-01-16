package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.dto.task.TaskDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
                SELECT 
                new com.alkl1m.taskmanager.dto.task.TaskDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.status)
                FROM Task b
                WHERE b.user = :user and  b.project = :project
            """)
    Page<TaskDto> findTasks(@Param("user") User user, @Param("project") Project project, Pageable pageable);
}
