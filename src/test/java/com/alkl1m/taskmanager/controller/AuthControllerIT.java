package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    void signupUserWithValidRequest_returnsValidMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "name": "Jane Doe",
                        "email": "jane@example.com",
                        "password": "password"
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    @Sql("/sql/user.sql")
    void signupUserWithExistingPayloadReturnsBadRequest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "password": "password"
                }
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Sql("/sql/user.sql")
    void loginUserWithValidRequest_ReturnsValidResponseMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "email": "john.doe@example.com",
                    "password": "password"
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully!"));
    }

    @Test
    @Sql("/sql/user.sql")
    void refreshTokenWithValidRequest_ReturnsValidMessageAndStatus() throws Exception {
        MvcResult loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "email": "john.doe@example.com",
                    "password": "password"
                }
                """))
                .andReturn();
        String refreshToken = loginResult.getResponse().getCookie("jwt-refresh").getValue();
        String jwt = loginResult.getResponse().getCookie("jwt").getValue();

        String requestBody = "{\"refreshToken\": \"" + refreshToken + "\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .cookie(new Cookie("jwt-refresh", refreshToken), new Cookie("jwt", jwt)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("Token is refreshed successfully!")
                );
    }

    @Test
    @Sql("/sql/user.sql")
    void logoutUserWithValidRequest_ReturnValidMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You've been signed out!"));
    }

}
