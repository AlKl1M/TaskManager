package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
class ProjectControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    ProjectRepository projectRepository;
    User user;
    Project project1;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name(UUID.randomUUID().toString().substring(0, 8))
                .email(UUID.randomUUID().toString().substring(0, 8))
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
        userRepository.save(user);
        projectRepository.save(project1);

        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        projectRepository.deleteAll();
        userRepository.delete(user);
        SecurityContextHolder.clearContext();
    }


    @Test
    void testGetAllProjectsWithNullQuery_ReturnsValidEntity() throws Exception {
        mockMvc.perform(get("/api/user/projects"))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.length()").value(1),
                    jsonPath("$[0].id").exists(),
                    jsonPath("$[0].name").value("test1 project")
                );
    }

    @Test
    void getAllProjectsWithQuery_ReturnsValidEntity() throws Exception {
        mockMvc.perform(get("/api/user/projects?query=t"))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.length()").value(1),
                    jsonPath("$[0].id").exists(),
                    jsonPath("$[0].name").value("test1 project")
                );
    }

    @Test
    void getAllProjectsWithZeroProjects_ReturnsValidEntity() throws Exception {
        mockMvc.perform(get("/api/user/projects?query=noProjectsExists"))
                .andExpectAll(
                    status().isOk(),
                    jsonPath("$.length()").value(0)
                );
    }

    @Test
    void createNewProjectWithValidPayload_ReturnsValidEntity() throws Exception {
        mockMvc.perform(post("/api/user/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "project 1",
                                    "description": "project 1 description"
                                }
                                """))
                .andExpect(
                        status().isCreated()
                );
    }

    @Test
    public void updateNewProjectWithValidPayload_ReturnsValidStatus() throws Exception {
        mockMvc.perform(put("/api/user/projects/{id}", project1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "name": "new project 1",
                    "description": "new project 1 description"
                }
                """))
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    public void deleteProjectWithExistingProjects_ReturnsValidResponse() throws Exception {
        mockMvc.perform(delete("/api/user/projects/{id}", project1.getId()))
                .andExpect(status().isOk());
        assertFalse(projectRepository.existsById(
                project1.getId())
        );
    }

    @Test
    public void changeStatusWithExistingProjects_ReturnsValidResponse() throws Exception {
        mockMvc.perform(put("/api/user/projects/{id}/changeStatus", project1.getId()))
                .andExpect(status().isOk());
        Project updatedProject = projectRepository.findById(project1.getId()).orElse(null);
        assertNotNull(updatedProject);
        assertEquals(Status.DONE, updatedProject.getStatus());
    }
}