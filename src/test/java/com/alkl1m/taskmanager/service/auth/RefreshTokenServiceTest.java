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
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private RefreshTokenService refreshTokenService;
    private User user;
    private RefreshToken refreshToken;
    private RefreshToken expiredToken;

    @BeforeEach
    public void setup() {
        refreshTokenService.setRefreshTokenDurationMs(3600000L);
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(true)
                .build();
         refreshToken = RefreshToken.builder()
                 .id(1L)
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenService.getRefreshTokenDurationMs()))
                .build();
        expiredToken = RefreshToken.builder()
                .id(2L)
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().minusMillis(1000))
                .build();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldReturnNewToken_WhenUserExistsAndRefreshTokenIsValid() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);
        RefreshToken result = refreshTokenService.createRefreshToken(user.getId());
        assertEquals(user, result.getUser());
        assertNotNull(result.getToken());
    }

    @Test
    public void shouldVerifyExpiration_WhenExpiredTokenThrowsTokenRefreshException() {
        assertThrows(TokenRefreshException.class, () -> {
            refreshTokenService.verifyExpiration(expiredToken);
        });
        verify(refreshTokenRepository, times(1)).delete(expiredToken);
    }

    @Test
    public void shouldVerifyExpiration_WhenValidTokenReturnsToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);
        assertEquals(refreshToken, result);
        verify(refreshTokenRepository, never()).delete(refreshToken);
    }

}