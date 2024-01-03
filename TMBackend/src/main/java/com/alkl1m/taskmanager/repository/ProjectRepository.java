package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByNameContaining(String name);
}
