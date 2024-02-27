package com.alkl1m.taskmanager.service.user;

import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.entity.VerificationToken;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.repository.VerificationTokenRepository;
import com.alkl1m.taskmanager.service.PasswordResetToken.PasswordResetTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Calendar;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenService passwordResetTokenService;
    @Mock
    private VerificationTokenRepository tokenRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(false)
                .build();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    public void shouldReturnOptionalOfUser_WhenUserExists() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Optional<User> actualUser = userService.findByEmail(user.getEmail());
        assertTrue(actualUser.isPresent());
        assertEquals(user, actualUser.get());
    }

    @Test
    public void shouldCallPasswordResetTokenService_userExists() {
        User user = new User();
        String passwordToken = "passwordToken";
        userService.createPasswordResetTokenForUser(user, passwordToken);
        verify(passwordResetTokenService).createPasswordResetTokenForUser(user, passwordToken);
    }

    @Test
    public void shouldReturnValid_WhenTokenIsValid() {
        String validToken = "valid-token";
        VerificationToken token = new VerificationToken(validToken, user);
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.HOUR_OF_DAY, 1);
        token.setExpirationTime(expirationTime.getTime());
        when(tokenRepository.findByToken(validToken)).thenReturn(token);
        boolean result = userService.validateToken(validToken);
        assertTrue(result);
        assertTrue(user.getEnabled());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void shouldReturnInvalidVerificationToken_WhenTokenInvalid() {
        String invalidToken = "invalid-token";
        when(tokenRepository.findByToken(invalidToken)).thenReturn(null);
        boolean result = userService.validateToken(invalidToken);
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void shouldReturnVerificationLinkExpired_WhenTokenExpired() {
        String expiredToken = "expired-token";
        VerificationToken token = new VerificationToken(expiredToken, user);
        Calendar expirationTime = Calendar.getInstance();
        expirationTime.add(Calendar.DAY_OF_MONTH, -1);
        token.setExpirationTime(expirationTime.getTime());
        when(tokenRepository.findByToken(expiredToken)).thenReturn(token);
        boolean result = userService.validateToken(expiredToken);
        assertFalse(result);
        assertFalse(user.getEnabled());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void UserService_SaveVerificationToken(){
        String verificationToken = "token";
        VerificationToken token = new VerificationToken(verificationToken, user);
        when(tokenRepository.save(any(VerificationToken.class))).thenReturn(token);
        userService.saveUserVerificationToken(user, verificationToken);
        verify(tokenRepository).save(any(VerificationToken.class));
    }
}