package com.alkl1m.taskmanager.service.PasswordResetToken;

import com.alkl1m.taskmanager.entity.PasswordResetToken;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    public void createPasswordResetTokenForUser(User user, String passwordToken){
        PasswordResetToken passwordResetToken = new PasswordResetToken(user, passwordToken);
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public boolean validatePasswordResetToken(String theToken){
        PasswordResetToken token = passwordResetTokenRepository.findByToken(theToken);
        if (token == null){
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        return (token.getExpiryDate().getTime() - calendar.getTime().getTime()) > 0;
    }

    public Optional<User> findUserByPasswordToken(String passwordToken){
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordToken).getUser());
    }

}
