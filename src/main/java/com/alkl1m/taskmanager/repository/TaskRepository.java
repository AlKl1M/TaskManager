package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.controller.payload.task.TaskDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("""
                SELECT 
                new com.alkl1m.taskmanager.controller.payload.task.TaskDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.deadline, b.status, b.tags)
                FROM Task b
                WHERE b.user.id = :userId and b.project = :project and b.tags LIKE %:tag%
            """)
    List<TaskDto> getAllTasksByTag(Long userId, Project project, @Param("tag") String tag);

    @Query("""
        SELECT
        new com.alkl1m.taskmanager.controller.payload.task.TaskDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.deadline, b.status, b.tags)
        FROM Task b
        WHERE b.user.id = :userId AND b.project = :project AND (b.name LIKE %:searchWord% OR b.description LIKE %:searchWord%)
""")
    List<TaskDto> getAllTasksBySearchWord(Long userId, Project project, @Param("searchWord") String searchWord);

    @Query("""
        SELECT
        new com.alkl1m.taskmanager.controller.payload.task.TaskDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.deadline, b.status, b.tags)
        FROM Task b
        WHERE b.user.id = :userId AND b.project = :project
""")
    List<TaskDto> getAllTasks(Long userId, Project project);

    Task getTaskById(Long id);
    List<Task> findTop50ByUserIdOrderByDeadlineAsc(Long userId);
    int countByProjectIdAndStatus(Long id, Status status);
    int countByProjectId(Long id);
}
