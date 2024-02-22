package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.service.dashboard.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
class DashboardControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    DashboardService dashboardService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskRepository taskRepository;
    User user;
    Project project1;
    Task task1;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("test")
                .email("test@mail.com")
                .password(encoder.encode("123"))
                .role(Role.USER)
                .enabled(true)
                .build();
        project1 = Project.builder()
                .name("test1 project")
                .description("test1 project description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .build();
        task1 = Task.builder()
                .name("test1 task")
                .description("test1 task description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .project(project1)
                .build();
        userRepository.save(user);
        projectRepository.save(project1);
        taskRepository.save(task1);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void handleGetDashboardData_RequestIsValid_ReturnsValidResponseEntity() throws Exception {
        mockMvc.perform(get("/api/user/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isArray())
                .andExpect(jsonPath("$.tasks").isArray());
    }
}