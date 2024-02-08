package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE (p.name LIKE %:query% OR p.description LIKE %:query%) AND p.user.id = :userId")
    List<Project> findByQueryAndUserId(@Param("query") String query, @Param("userId") Long userId);
    List<Project> findAllByUserId(Long userId);
    List<Project> getAllProjectsByUserId(Long id);
    Project getProjectById(Long id);

}
