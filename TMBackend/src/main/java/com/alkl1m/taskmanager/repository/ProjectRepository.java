package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.dto.project.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
                SELECT 
                new com.alkl1m.taskmanager.dto.project.ProjectDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.status)
                FROM Project b
                WHERE b.user.id = :userId
            """)
    Optional<Page<ProjectDto>> findProjects(@Param("userId") Long userId, Pageable pageable);

    Optional<Project> findByIdAndUserId(Long projectId, Long userId);
}
