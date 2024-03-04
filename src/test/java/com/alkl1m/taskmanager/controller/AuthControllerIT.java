package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.dto.auth.LoginRequest;
import com.alkl1m.taskmanager.dto.auth.SignupRequest;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.repository.RefreshTokenRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
class AuthControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Test
    public void signupUserWithValidRequest_returnsValidMessage() throws Exception {
        SignupRequest signupRequest = new SignupRequest("Jane Doe", "jane@example.com", "password");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));
    }

    @Test
    public void signupUserWithExistingEmail_ReturnsBadRequest() throws Exception {
        User user = User.builder()
                .name("John")
                .email("john@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        SignupRequest signupRequest = new SignupRequest("Johnn Doe", "john@example.com", "password");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void signupUserWithExistingName_ReturnsBadRequest() throws Exception {
        User user = User.builder()
                .name("John Doe")
                .email("johnn@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        SignupRequest signupRequest = new SignupRequest("John Doe", "john@example.com", "password");
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(signupRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void loginUserWithValidRequest_ReturnsValidResponseMessage() throws Exception {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        User user = User.builder()
                .name("test")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User logged in successfully!"));
        mockMvc.perform(post("/api/auth/logout"));
        refreshTokenRepository.deleteAll();
    }

    @Test
    public void refreshTokenWithValidRequest_ReturnsValidMessageAndStatus() throws Exception {
        User user = User.builder()
                .name("Bob")
                .email("bob@example.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        LoginRequest loginRequest = new LoginRequest("bob@example.com", "password");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andReturn();
        String refreshToken = loginResult.getResponse().getCookie("jwt-refresh").getValue();
        String jwt = loginResult.getResponse().getCookie("jwt").getValue();
        System.out.println(jwt);

        String requestBody = "{\"refreshToken\": \"" + refreshToken + "\"}";

        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .cookie(new Cookie("jwt-refresh", refreshToken), new Cookie("jwt", jwt)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token is refreshed successfully!"));
        mockMvc.perform(post("/api/auth/logout"));
        refreshTokenRepository.deleteAll();
    }

    @Test
    public void refreshTokenWithInvalidPayload_ReturnsBadRequestMessage() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(loginRequest)))
                .andReturn();

        String requestBody = "{}";
        mockMvc.perform(post("/api/auth/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Refresh Token is empty!"));
    }

    @Test
    public void logoutUserWithValidRequest_ReturnValidMessage() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You've been signed out!"));
    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}