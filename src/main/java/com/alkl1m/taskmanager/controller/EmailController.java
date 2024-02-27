package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.auth.MessageResponse;
import com.alkl1m.taskmanager.dto.auth.PasswordRequestUtil;
import com.alkl1m.taskmanager.dto.auth.PasswordResetDto;
import com.alkl1m.taskmanager.dto.auth.PasswordResetRequestDto;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.event.listener.RegistrationCompleteEventListener;
import com.alkl1m.taskmanager.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class EmailController {
    private final UserService userService;
    private final RegistrationCompleteEventListener eventListener;

    @GetMapping("/verifyEmail")
    public ResponseEntity<?> sendVerificationToken(@RequestParam("token") String token){
        boolean verificationResult = userService.validateToken(token);
        if (verificationResult){
            log.info("User verified account by email successfully");
        } else {
            log.warn("User trying to use invalid verification link to verify account");
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User trying to use invalid verification link to verify account"));
        }
        return ResponseEntity.ok()
                .body(new MessageResponse("User verified account by email successfully"));
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<?> resetPasswordRequest(@RequestBody PasswordResetRequestDto passwordResetRequestDto,
                                       final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Received password reset request");
        Optional<User> user = userService.findByEmail(passwordResetRequestDto.email());
        String passwordResetUrl;
        if (user.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
            passwordResetUrl = passwordResetEmailLink(applicationUrl(request), passwordResetToken);
            eventListener.sendPasswordResetVerificationEmail(passwordResetUrl);
            log.info("Password reset token created for user: {}", user.get().getEmail());
            log.info("Password reset URL: {}", passwordResetUrl);
        } else {
            log.warn("User not found with email: {}", passwordResetRequestDto.email());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User not found with email " + passwordResetRequestDto.email()));
        }
        return ResponseEntity.ok()
                .body(new MessageResponse("We send email with link for reset password to " + passwordResetRequestDto.email()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto passwordResetDto,
                              @RequestParam("token") String passwordResetToken){
        boolean tokenVerificationResult = userService.validatePasswordResetToken(passwordResetToken);
        if (!tokenVerificationResult) {
            log.error("Invalid token password reset token");
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid token password reset token"));
        }
        Optional<User> theUser = Optional.ofNullable(userService.findUserByPasswordToken(passwordResetToken));
        log.debug("Is user present: " + theUser.isPresent() + ", User name: " + theUser.get().getName());
        userService.changePassword(theUser.get(), passwordResetDto.password());
        log.info("Password has been reset successfully");
        return ResponseEntity.ok()
                .body(new MessageResponse("Password has been reset successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequestUtil requestUtil){
        User user = userService.findByEmail(requestUtil.email()).get();
        if (!userService.oldPasswordIsValid(user, requestUtil.oldPassword())){
            log.error("Incorrect old password");
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Incorrect old password"));
        }
        userService.changePassword(user, requestUtil.newPassword());
        log.info("Password changed successfully");
        return ResponseEntity.ok()
                .body(new MessageResponse("Password changed successfully"));
    }
    private String passwordResetEmailLink(String applicationUrl, String passwordResetToken){
        return applicationUrl + "/api/auth/reset-password?token=" + passwordResetToken;
    }
    public String applicationUrl(HttpServletRequest request){
        return "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();
    }
}
