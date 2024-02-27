package com.alkl1m.taskmanager.service.PasswordResetToken;

import com.alkl1m.taskmanager.entity.PasswordResetToken;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.repository.PasswordResetTokenRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetTokenServiceTest {
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PasswordResetTokenServiceImpl passwordResetTokenService;
    private PasswordResetToken passwordResetToken;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(true)
                .build();
        passwordResetToken = PasswordResetToken.builder()
                .id(1L)
                .user(user)
                .token("token")
                .expiryDate(Date.from(Instant.now()))
                .build();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void PasswordResetTokenService_CreatePasswordResetTokenForUser(){
        String passwordToken = "token";
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(passwordResetToken);
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordToken);
        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }
    @Test
    public void PasswordResetTokenService_ValidatePasswordResetToken_ReturnsValid(){
        String passwordToken = "token";
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetToken);
        boolean PasswordValidResult = passwordResetTokenService.validatePasswordResetToken(passwordToken);
        assertTrue(PasswordValidResult);
    }
    @Test
    public void PasswordResetTokenService_ValidatePasswordResetToken_ReturnsInvalidPRToken(){
        String passwordToken = "token";
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(null);
        boolean PasswordValidResult = passwordResetTokenService.validatePasswordResetToken(passwordToken);
        assertFalse(PasswordValidResult);
    }

    @Test
    public void PasswordResetTokenService_FindUserByPasswordToken_ReturnsUser(){
        String passwordToken = "token";
        when(passwordResetTokenRepository.findByToken(passwordToken)).thenReturn(passwordResetToken);
        Optional<User> userResult = passwordResetTokenService.findUserByPasswordToken(passwordToken);
        assertEquals(userResult.get().getId(), user.getId());
        assertEquals(userResult.get().getName(), user.getName());
    }
}