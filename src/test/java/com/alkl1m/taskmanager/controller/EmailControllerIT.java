package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;
import com.alkl1m.taskmanager.dto.auth.PasswordRequestUtil;
import com.alkl1m.taskmanager.dto.auth.PasswordResetDto;
import com.alkl1m.taskmanager.dto.auth.PasswordResetRequestDto;
import com.alkl1m.taskmanager.entity.*;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.event.RegistrationCompleteEvent;
import com.alkl1m.taskmanager.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
public class EmailControllerIT {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    ApplicationEventPublisher publisher;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    VerificationTokenRepository verificationTokenRepository;
    User user;
    @BeforeEach
    public void setUp(){
        user = User.builder()
                .id(1L)
                .name(UUID.randomUUID().toString().substring(0, 8))
                .email("example@example.com")
                .password(encoder.encode("123"))
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, "http://localhost:8080/signup"));
    }

    @AfterEach
    public void tearDown(){
        verificationTokenRepository.deleteAll();
        passwordResetTokenRepository.deleteAll();
        userRepository.deleteAll();
    }
    @Test
    public void resetPasswordRequest_ReturnsValidResponse() throws Exception {
        PasswordResetRequestDto passwordResetRequestDto = new PasswordResetRequestDto("example@example.com");
        mockMvc.perform(post("/api/auth/password-reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordResetRequestDto))
                ).andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("We send email with link for reset password to " + "example@example.com")
                );
    }

    @Test
    public void resetPasswordRequest_ReturnsInvalidUserNotFound() throws Exception {
        PasswordResetRequestDto passwordResetRequestDto = new PasswordResetRequestDto("NOexample@example.com");
        mockMvc.perform(post("/api/auth/password-reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordResetRequestDto))
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("User not found with email " + "NOexample@example.com")
        );
    }

    @Test
    public void sendVerificationToken_ReturnsValidResponse() throws Exception {
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        mockMvc.perform(get("/api/auth/verifyEmail?token={verificationToken}", verificationToken.getToken()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("User verified account by email successfully")
                );
    }
    @Test
    public void sendVerificationToken_ReturnsInvalidVerificationToken() throws Exception {
        String verificationToken = UUID.randomUUID().toString();
        mockMvc.perform(get("/api/auth/verifyEmail?token={verificationToken}", verificationToken))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.message").value("User trying to use invalid verification link to verify account")
                );
    }
    @Test
    public void resetPassword_ReturnsValidResponse() throws Exception {
        String passwordToken = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, passwordToken);
        passwordResetTokenRepository.save(passwordResetToken);
        PasswordResetDto passwordResetDto = new PasswordResetDto("example@example.com", "456");
        mockMvc.perform(post("/api/auth/reset-password?token={passwordResetToken}", passwordResetToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordResetDto))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.message").value("Password has been reset successfully")
        );
    }

    @Test
    public void resetPassword_ReturnsInValidPasswordResetToken() throws Exception {
        String passwordToken = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, passwordToken);
        passwordResetTokenRepository.save(passwordResetToken);
        PasswordResetDto passwordResetDto = new PasswordResetDto("example@example.com", "456");
        mockMvc.perform(post("/api/auth/reset-password?token={passwordResetToken}", (Object) null)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordResetDto))
        ).andExpectAll(
                status().isBadRequest(),
                jsonPath("$.message").value("Invalid token password reset token")
        );
    }

    @Test
    public void changePassword_ReturnsValidResponse() throws Exception {
        PasswordRequestUtil passwordRequestUtil = new PasswordRequestUtil("example@example.com", "123", "456");
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordRequestUtil))
        ).andExpectAll(status().isOk(),
                jsonPath("$.message").value("Password changed successfully"));
    }
    @Test
    public void changePassword_ReturnsInvalidOldPassword() throws Exception {
        PasswordRequestUtil passwordRequestUtil = new PasswordRequestUtil("example@example.com", "NO123", "456");
        mockMvc.perform(post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordRequestUtil))
        ).andExpectAll(status().isBadRequest(),
                jsonPath("$.message").value("Incorrect old password"));
    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
