package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.TaskRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
@Transactional
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
        Optional<User> optionalUser = userRepository.findById(1L);
        user = optionalUser.get();
        project = projectRepository.getProjectById(1L);
        task1 = taskRepository.getTaskById(1L);
        task2 = taskRepository.getTaskById(2L);
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @Sql("/sql/user_and_project_and_tasks.sql")
    public void getAllTaskBySearchWord_ReturnsTasks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/projects/{projectId}/getAllTasksBySearchWord?searchWord=Task 1",
                        project.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Task 1")
                );
    }
    @Test
    @Sql("/sql/user_and_project_and_tasks.sql")
    public void getAllTaskByTag_ReturnsTasks() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/projects/{projectId}/getAllTasksByTag?tag=tag1",
                        project.getId()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Task 1")
                );
    }
    @Test
    @Sql("/sql/user_and_project_and_tasks.sql")
    public void getAllTaskBySearchWord_ReturnsEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/projects/{projectId}/getAllTasksBySearchWord?searchWord=RandomWord",
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
    @Sql("/sql/user_and_projects.sql")
    public void createTask_ReturnValidResponseTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects/{projectId}/tasks", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Home",
                                "description": "TestTask2Description",
                                "deadline": "2024-04-10T10:10:05+09:00",
                                "tags": ["tag2"]
                            }
                            """))
                .andExpect(
                        status().isCreated()
                );
    }
    @Test
    @Sql("/sql/user_and_projects.sql")
    public void createTask_ReturnInvalidResponseTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects/{projectId}/tasks", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Home",
                                "description": "TestTask2Description",
                                "deadline": "2024-04-10T10:10:05+09:00",
                                "tags": ["tag1","tag2","tag3", "tag4"]
                            }
                            """))
                .andExpect(
                        status().isBadRequest()
                );
    }
    @Test
    @Sql("/sql/user_and_projects.sql")
    public void createTask_ReturnValidResponseWithDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects/{projectId}/tasks", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Home",
                                "description": "TestTask2Description",
                                "deadline": "2024-04-10T10:10:05+09:00",
                                "tags": ["tag2"]
                            }
                            """))
                .andExpect(
                        status().isCreated()
                );
    }
    @Test
    @Sql("/sql/user_and_projects.sql")
    public void createTask_ReturnValidResponseWithOutDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects/{projectId}/tasks", project.getId())
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
    @Sql("/sql/user_and_projects.sql")
    public void createTask_ReturnInvalidOldDate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects/{projectId}/tasks", project.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Home",
                                "description": "TestTask2Description",
                                "deadline": "2022-04-10T10:10:05+09:00",
                                "tags": ["tag2"]
                            }
                            """))
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    @Sql("/sql/user_and_project_and_tasks.sql")
    public void updateTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/projects/{projectId}/tasks/{taskId}", project.getId(), task2.getId())
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
    @Sql("/sql/user_and_project_and_tasks.sql")
    public void deleteTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/projects/{projectId}/tasks/{taskId}", project.getId(),task2.getId()))
                .andExpect(
                        status().isNoContent()
                );
    }
    @Test
    @Sql("/sql/user_and_project_and_tasks.sql")
    public void changeStatusForTask_ReturnValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/projects/{projectId}/tasks/{taskId}/done", project.getId(),task2.getId()))
                .andExpect(
                        status().isOk()
                );
    }

}