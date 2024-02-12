package com.alkl1m.taskmanager.service.user;

import com.alkl1m.taskmanager.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);

    void createPasswordResetTokenForUser(User user, String passwordToken);

    void saveUserVerificationToken(User theUser, String verificationToken);

    String validatePasswordResetToken(String passwordResetToken);

    User findUserByPasswordToken(String passwordResetToken);

    String validateToken(String token);

    void changePassword(User user, String s);

    boolean oldPasswordIsValid(User user, String s);
}
