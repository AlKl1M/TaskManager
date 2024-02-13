package com.alkl1m.taskmanager.service.auth;

import com.alkl1m.taskmanager.entity.RefreshToken;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.exception.TokenRefreshException;
import com.alkl1m.taskmanager.repository.RefreshTokenRepository;
import com.alkl1m.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        user = User.builder().name("user").email("user@mail.com").password("123").role(Role.USER).build();
        refreshToken = RefreshToken.builder().user(user).expiryDate(Instant.now()).token(UUID.randomUUID().toString()).build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void RefreshTokenService_WhenTokenExists_ReturnRefreshToken() {
        String token = refreshToken.getToken();

        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);
        assertTrue(result.isPresent());
        assertEquals(refreshToken, result.get());
        verify(refreshTokenRepository, times(1)).findByToken(token);
    }

    @Test
    public void RefreshTokenService_WhenTokenDoesNotExist_ReturnEmptyOptional() {
        String token = "nonexistent_token";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());
        Optional<RefreshToken> result = refreshTokenService.findByToken(token);
        assertFalse(result.isPresent());
        verify(refreshTokenRepository, times(1)).findByToken(token);
    }

    @Test
    public void testCreateRefreshToken_ReturnRefreshToken() {
        //Todo
    }

    @Test
    public void testVerifyExpiration_WhenTokenNotExpired_ReturnToken() {
        //Todo
    }

    @Test
    public void testVerifyExpiration_WhenTokenExpired_ThrowTokenRefreshException() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000));
        assertThrows(TokenRefreshException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository, times(1)).delete(refreshToken);
    }

    @Test
    public void testDeleteByUserId_ReturnNumberOfDeletedTokens() {
        Long userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.deleteByUser(user)).thenReturn(2);
        int result = refreshTokenService.deleteByUserId(userId);
        assertEquals(2, result);
        verify(userRepository, times(1)).findById(userId);
        verify(refreshTokenRepository, times(1)).deleteByUser(user);
    }
}