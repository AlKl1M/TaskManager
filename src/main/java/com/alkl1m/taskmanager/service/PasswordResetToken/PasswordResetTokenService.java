package com.alkl1m.taskmanager.service.PasswordResetToken;

import com.alkl1m.taskmanager.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface PasswordResetTokenService {
    void createPasswordResetTokenForUser(User user, String passwordToken);
    boolean validatePasswordResetToken(String token);
    Optional<User> findUserByPasswordToken(String passwordToken);
}
