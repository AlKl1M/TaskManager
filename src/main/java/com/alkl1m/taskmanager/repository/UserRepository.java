package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    @Query("SELECT COUNT(p) FROM User u JOIN u.projects p WHERE u.id = :userId")
    int countUserProjects(@Param("userId") Long userId);
    boolean existsByName(String name);

    Optional<User> findByEmailOrName(String email, String name);

    boolean existsByEmailOrName(String email, String name);
}
