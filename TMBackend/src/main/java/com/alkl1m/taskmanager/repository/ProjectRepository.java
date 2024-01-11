package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.dto.ProjectDto;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("""
                SELECT 
                new com.alkl1m.taskmanager.dto.ProjectDto(b.id, b.name, b.description, b.createdAt, b.doneAt, b.status)
                FROM Project b
                WHERE b.user = :user
            """)
    Page<ProjectDto> findProjects(@Param("user") User user, Pageable pageable);
}
