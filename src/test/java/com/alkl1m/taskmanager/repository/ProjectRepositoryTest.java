package com.alkl1m.taskmanager.repository;

import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
class ProjectRepositoryTest {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private Project project;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("First");
        user.setPassword("123");
        user.setEmail("First@mail.com");
        user.setRole(Role.USER);
        userRepository.save(user);
        Project project1 = Project.builder()
                .name("Test Project")
                .description("Test Description")
                .createdAt(Instant.now())
                .status(Status.IN_WORK)
                .user(user)
                .build();
        projectRepository.save(project1);
    }

    @Test
    void findByQueryAndUserId_testWithNotFullyName() {
        List<Project> projects = projectRepository.findByQueryAndUserId("Tes", 1L);
        assertEquals(projects.size(), 1);
        assertEquals(projects.get(0).getName(), "Test Project");
        assertEquals(projects.get(0).getDescription(), "Test Description");
        assertEquals(projects.get(0).getUser().getId(), 1L);
    }
}