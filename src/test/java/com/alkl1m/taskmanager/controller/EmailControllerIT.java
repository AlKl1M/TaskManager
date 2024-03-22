package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.TestBeans;

import com.alkl1m.taskmanager.controller.exception.InvalidVerificationTokenException;
import com.alkl1m.taskmanager.controller.payload.auth.PasswordRequestUtil;
import com.alkl1m.taskmanager.controller.payload.auth.PasswordResetDto;
import com.alkl1m.taskmanager.controller.payload.auth.PasswordResetRequestDto;
import com.alkl1m.taskmanager.entity.*;
import com.alkl1m.taskmanager.event.RegistrationCompleteEvent;
import com.alkl1m.taskmanager.repository.*;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestBeans.class)
@AutoConfigureMockMvc
@Transactional
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
        Optional<User> optionalUser = userRepository.findById(1L);
        user = optionalUser.get();
        UserDetailsImpl userDetails = UserDetailsImpl.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        publisher.publishEvent(new RegistrationCompleteEvent(user, "http://localhost:8080/signup"));
    }

    @Test
    @Sql("/sql/user.sql")
    public void resetPasswordRequest_ReturnsValidResponse() throws Exception {
        PasswordResetRequestDto passwordResetRequestDto = new PasswordResetRequestDto("john.doe@example.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password-reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(passwordResetRequestDto))
                ).andExpectAll(
                        status().isOk()
                );
    }

    @Test
    @Sql("/sql/user.sql")
    public void resetPasswordRequest_ReturnsInvalidUserNotFound() throws Exception {
        PasswordResetRequestDto passwordResetRequestDto = new PasswordResetRequestDto("NOexample@example.com");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/password-reset-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordResetRequestDto))
        ).andExpectAll(
                status().isBadRequest(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                content().json("""
                            {
                                "Error": 
                                    "User not found with email: NOexample@example.com"  
                            }
                        """)
        );
        Assertions.assertNotEquals(passwordResetRequestDto.email(), user.getEmail());
    }

    @Test
    @Sql("/sql/user.sql")
    public void sendVerificationToken_ReturnsValidResponse() throws Exception {
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/verifyEmail?token={verificationToken}", verificationToken.getToken()))
                .andExpectAll(
                        status().isOk()
                );
    }
    @Test
    @Sql("/sql/user.sql")
    public void sendVerificationToken_ReturnsInvalidVerificationToken()
            throws Exception {
        String verificationToken = UUID.randomUUID().toString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/verifyEmail?token={verificationToken}", verificationToken))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                {
                                "Error":
                                    "User trying to use invalid verification link to verify account"
                                 }
                                    """)
                );
    }
    @Test
    @Sql("/sql/user.sql")
    public void resetPassword_ReturnsValidResponse() throws Exception {
        String passwordToken = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, passwordToken);
        passwordResetTokenRepository.save(passwordResetToken);
        PasswordResetDto passwordResetDto = new PasswordResetDto("example@example.com", "456");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/reset-password?token={passwordResetToken}", passwordResetToken.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordResetDto))
        ).andExpectAll(
                status().isOk()
        );
    }

    @Test
    @Sql("/sql/user.sql")
    public void resetPassword_ReturnsInValidPasswordResetToken() throws Exception {
        String passwordTokenUnused = UUID.randomUUID().toString();
        PasswordResetDto passwordResetDto = new PasswordResetDto("example@example.com", "456");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/reset-password?token={passwordResetToken}", passwordTokenUnused)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordResetDto))
        ).andExpectAll(
                status().isBadRequest(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                content().json("""
                            {
                                "Error": 
                                    "User try to reset password with invalid password reset token"  
                            }
                        """)
        );
        Assertions.assertNull(passwordResetTokenRepository.findByToken(passwordTokenUnused));
    }

    @Test
    @Sql("/sql/user.sql")
    public void changePassword_ReturnsValidResponse() throws Exception {
        String password = "password";
        PasswordRequestUtil passwordRequestUtil = new PasswordRequestUtil(user.getEmail(), password, "456");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordRequestUtil))
        ).andExpectAll(
                status().isOk()
        );
    }
    @Test
    @Sql("/sql/user.sql")
    public void changePassword_ReturnsInvalidOldPassword() throws Exception {
        String password = "password";
        PasswordRequestUtil passwordRequestUtil = new PasswordRequestUtil(user.getEmail(), "NO"+password, "456");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(passwordRequestUtil))
        ).andExpectAll(
                status().isBadRequest(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                content().json("""
                            {
                                "Error": 
                                    "Exception of trying to change password with incorrect old password"  
                            }
                        """)
        );

    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
