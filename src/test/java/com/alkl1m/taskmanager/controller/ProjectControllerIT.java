package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.entity.User;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
@Transactional
class ProjectControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    User user;

    @BeforeEach
    void setUp() {
        Optional<User> optionalUser = userRepository.findById(1L);
        user = optionalUser.get();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @Sql("/sql/user_and_projects.sql")
    void testGetAllProjectsWithNullQuery_ReturnsValidEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/projects"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(5),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Project 1")
                );
    }

    @Test
    @Sql("/sql/user_and_projects.sql")
    void getAllProjectsWithQuery_ReturnsValidEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/projects?query=5"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.length()").value(1),
                        jsonPath("$[0].id").exists(),
                        jsonPath("$[0].name").value("Project 5")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void createNewProjectWithValidPayload_ReturnsValidEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                    "name": "project 6",
                    "description": "project 6 description"
                    }
                """))
                .andExpect(
                        status().isCreated()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void createNewProjectWithInvalidPayload_ReturnsValidEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/user/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                    "name": "  ",
                    "description": null
                    }
                """))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                            {
                                "errors": 
                                    ["Project description must not be null",
                                    "Project name must have size from 3 to 60",
                                    "Project name must not be empty",
                                    "Project description must not be empty"
                                    ]
                            }
                        """)
                );
    }

    @Test
    @Sql("/sql/user_and_projects.sql")
    void updateProjectWithValidPayload_ReturnsValidStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/projects/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "name": "updated project name",
                    "description": "updated project description"
                }
                """))
                .andExpect(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/user_and_projects.sql")
    void updateProjectWithInvalidPayload_ReturnsValidEntity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/projects/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                    "name": "  ",
                    "description": null
                    }
                """))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                            {
                                "errors": 
                                    ["Project description must not be null",
                                    "Project name must have size from 3 to 60",
                                    "Project name must not be empty",
                                    "Project description must not be empty"
                                    ]
                            }
                        """)
                );
    }

    @Test
    @Sql("/sql/user_and_projects.sql")
    void deleteProjectWithExistingProject_ReturnsValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/projects/{id}", 1L))
                .andExpectAll(
                        status().isNoContent()
                );
    }

    @Test
    @Sql("/sql/user_and_projects.sql")
    void changeStatusWithExistingProjects_ReturnsValidResponse() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/projects/{id}/changeStatus", 1L))
                .andExpect(
                        status().isOk()
                );
    }

}