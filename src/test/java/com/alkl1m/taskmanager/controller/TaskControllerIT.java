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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.junit.jupiter.api.Assertions.*;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
public class TaskControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    TaskRepository taskRepository;
    User user;
    Project project;
    Task task1, task2;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .name(UUID.randomUUID().toString().substring(0, 8))
                .email(UUID.randomUUID().toString().substring(0, 8))
                .password(encoder.encode("123"))
                .role(Role.USER)
                .enabled(true)
                .build();
        project = Project.builder()
                .name("test1 project")
                .description("test1 project description")
                .status(Status.IN_WORK)
                .createdAt(Instant.now())
                .user(user)
                .build();
        task1 = Task.builder()
                .name("Apple")
                .description("TestTask1Description")
                .createdAt(Instant.now())
                .deadline(Instant.now())
                .status(Status.IN_WORK)
                .tags("tag1")
                .project(project)
                .user(user)
                .build();

        task2 = Task.builder()
                .name("Home")
                .description("TestTask2Description")
                .createdAt(Instant.now())
                .deadline(Instant.now())
                .status(Status.DONE)
                .tags("tag2")
                .project(project)
                .user(user)
                .build();

        userRepository.save(user);
        projectRepository.save(project);
        taskRepository.save(task1);
        taskRepository.save(task2);


        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    public void tearDown() {
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.delete(user);
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getAllTaskBySearchWord_ReturnsTasks() throws Exception {
        mockMvc.perform(get("/api/user/projects/{projectId}/getAllTasksBySearchWord?searchWord=Apple",
                        project.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[0].name").value("Apple")
                );
    }
    @Test
    public void getAllTaskByTag_ReturnsTasks() throws Exception {
        mockMvc.perform(get("/api/user/projects/{projectId}/getAllTasksByTag?tag=tag1",
                        project.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").value(1L),
                        jsonPath("$[0].name").value("Apple")
                );
    }
    @Test
    public void getAllTaskBySearchWord_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/user/projects/{projectId}/getAllTasksBySearchWord?searchWord=RandomWord",
                        project.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
        mockMvc.perform(get("/api/user/projects/{projectId}/getAllTasksByTag?tag=RandomTag",
                        project.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(0)
                );
    }
    @Test
    public void createTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(post("/api/user/projects/{projectId}/tasks", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Home",
                                "description": "TestTask2Description",
                                "tags": ["tag2"]
                            }
                            """))
                .andExpect(
                        status().isCreated()
                );
    }
    @Test
    public void updateTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(put("/api/user/projects/{projectId}/tasks/{taskId}", project.getId(),task2.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Home2",
                                "description": "TestTask2DescriptionUpdated",
                                "tags": ["tag2Updated"]
                            }
                            """))
                .andExpect(
                        status().isOk()
                );
    }
    @Test
    public void deleteTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(delete("/api/user/projects/{projectId}/tasks/{taskId}", project.getId(),task2.getId()))
                .andExpect(
                        status().isOk()
                );
    }
    @Test
    public void changeStatusForTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(put("/api/user/projects/{projectId}/tasks/{taskId}/done", project.getId(),task2.getId()))
                .andExpect(
                        status().isOk()
                );
    }

}
