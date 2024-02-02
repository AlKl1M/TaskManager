package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    Optional<User> findByUserRole(UserRole userRole);
    Optional<User> findByEmail(String email);
    Optional<User> findFirstById(Long userId);
}
