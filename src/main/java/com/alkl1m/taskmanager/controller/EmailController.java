package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.util.checker.BindingChecker;
import com.alkl1m.taskmanager.controller.exception.InvalidOldPasswordException;
import com.alkl1m.taskmanager.controller.exception.InvalidPasswordResetTokenException;
import com.alkl1m.taskmanager.controller.exception.InvalidVerificationTokenException;
import com.alkl1m.taskmanager.controller.exception.UserNotFoundException;
import com.alkl1m.taskmanager.controller.payload.auth.PasswordRequestUtil;
import com.alkl1m.taskmanager.controller.payload.auth.PasswordResetDto;
import com.alkl1m.taskmanager.controller.payload.auth.PasswordResetRequestDto;
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
            return ResponseEntity
                    .ok("User verified account by email successfully");
        } else {
            log.warn("User trying to use invalid verification link to verify account");
            throw new InvalidVerificationTokenException();
        }
    }

    @PostMapping("/password-reset-request")
    @BindingChecker
    public String resetPasswordRequest(@RequestBody PasswordResetRequestDto passwordResetRequestDto,
                                       final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Received password reset request");
        Optional<User> user = userService.findByEmail(passwordResetRequestDto.email());
        String passwordResetUrl = "";
        if (user.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
            passwordResetUrl = passwordResetEmailLink(applicationUrl(request), passwordResetToken);
            log.info("Password reset token created for user: {}", user.get().getEmail());
            log.info("Password reset URL: {}", passwordResetUrl);
        } else {
            log.warn("User not found with email: {}", passwordResetRequestDto.email());
            throw new UserNotFoundException(passwordResetRequestDto.email());
        }
        return passwordResetUrl;
    }

    @PostMapping("/reset-password")
    @BindingChecker
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto passwordResetDto,
                                           @RequestParam("token") String passwordResetToken){
        boolean tokenVerificationResult = userService.validatePasswordResetToken(passwordResetToken);
        if (!tokenVerificationResult) {
            log.error("User try to reset password with invalid password reset token");
            throw new InvalidPasswordResetTokenException();
        }
        else {
            Optional<User> theUser = Optional.ofNullable(userService.findUserByPasswordToken(passwordResetToken));
            log.debug("Is user present: " + theUser.isPresent() + ", User name: " + theUser.get().getName());
                userService.changePassword(theUser.get(), passwordResetDto.password());
                log.info("Password has been reset successfully");
                return ResponseEntity.ok("Password has been reset successfully");
        }
    }

    @PostMapping("/change-password")
    @BindingChecker
    public ResponseEntity<?> changePassword(@RequestBody PasswordRequestUtil requestUtil){
        User user = userService.findByEmail(requestUtil.email()).get();
        System.out.println(user.getPassword());
        if (!userService.oldPasswordIsValid(user, requestUtil.oldPassword())){
            log.error("Incorrect old password");
            throw new InvalidOldPasswordException();
        }
        else {
            userService.changePassword(user, requestUtil.newPassword());
            log.info("Password changed successfully");
            return ResponseEntity.ok("Password changed successfully");
        }
    }
    private String passwordResetEmailLink(String applicationUrl, String passwordResetToken)
            throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl + "/api/auth/reset-password?token=" + passwordResetToken;
        eventListener.sendPasswordResetVerificationEmail(url);
        return url;
    }
    public String applicationUrl(HttpServletRequest request){
        return "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();
    }
}
