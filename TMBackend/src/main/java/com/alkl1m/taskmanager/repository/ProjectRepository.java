package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAllByNameContaining(String name);

    List<Project> findByUser(User user);

    Optional<Project> findByUserEmailAndId(String email, Long projectId);
}
