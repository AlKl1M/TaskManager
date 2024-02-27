package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.dto.project.CreateProjectRequest;
import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.enums.Status;
import com.alkl1m.taskmanager.repository.ProjectRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    void testGetAllProjectsWhenQueryIsNull() throws Exception {
        mockMvc.perform(get("/api/user/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("test1 project"));
    }

    @Test
    void handleGetAllProjects_WithQuery_ReturnsValidResponse() throws Exception {
        mockMvc.perform(get("/api/user/projects?query=t"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(4))
                .andExpect(jsonPath("$[0].name").value("test1 project"));
    }

    @Test
    void handleGetAllProjects_WithZeroProjects_ReturnsValidEntity() throws Exception {
        mockMvc.perform(get("/api/user/projects?query=noProjectsExists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void handleCreateNewProject_PayloadIsValid_ReturnsValidResponse() throws Exception {
        CreateProjectRequest createProjectRequest =
                new CreateProjectRequest("project 1", "project 1 description");
        mockMvc.perform(post("/api/user/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createProjectRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void handleUpdateNewProject_PayloadIsValid_ReturnsValidStatus() throws Exception {
        CreateProjectRequest createProjectRequest =
                new CreateProjectRequest("project 1", "project 1 description");
        mockMvc.perform(put("/api/user/projects/{id}", project1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(createProjectRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void handleDeleteProject_ProjectExists_ReturnsValidResponseAndProjectExists() throws Exception {
        mockMvc.perform(delete("/api/user/projects/{id}", project1.getId()))
                .andExpect(status().isOk());
        assertFalse(projectRepository.existsById(project1.getId()));
    }

    @Test
    public void handleChangeStatus_ProjectExists_ReturnsValidResponseAndUpdateProject() throws Exception {
        mockMvc.perform(put("/api/user/projects/{id}/changeStatus", project1.getId()))
                .andExpect(status().isOk());
        Project updatedProject = projectRepository.findById(project1.getId()).orElse(null);
        assertNotNull(updatedProject);
        assertEquals(Status.DONE, updatedProject.getStatus());
    }
    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}