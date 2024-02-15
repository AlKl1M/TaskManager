package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.auth.PasswordRequestUtil;
import com.alkl1m.taskmanager.dto.auth.PasswordResetRequest;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.event.listener.RegistrationCompleteEventListener;
import com.alkl1m.taskmanager.service.user.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void sendVerificationToken(@RequestParam("token") String token){
        String verificationResult = userService.validateToken(token);
        if (verificationResult.equalsIgnoreCase("valid")){
            log.info("User verified account by email successfully");
        } else {
            log.warn("User trying to use invalid verification link to verify account");
        }
    }

    @PostMapping("/password-reset-request")
    public String resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest,
                                       final HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {
        log.info("Received password reset request");
        Optional<User> user = userService.findByEmail(passwordResetRequest.email());
        String passwordResetUrl = "";
        if (user.isPresent()) {
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(), passwordResetToken);
            passwordResetUrl = passwordResetEmailLink(user.get(), applicationUrl(request), passwordResetToken);
            log.info("Password reset token created for user: {}", user.get().getEmail());
            log.info("Password reset URL: {}", passwordResetUrl);
        } else {
            log.warn("User not found with email: {}", passwordResetRequest.email());
        }
        return passwordResetUrl;
    }

    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody PasswordResetRequest passwordResetRequest,
                                @RequestParam("token") String passwordResetToken){
        String tokenVerificationResult = userService.validatePasswordResetToken(passwordResetToken);
        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            log.error("Invalid token password reset token");
        }
        Optional<User> theUser = Optional.ofNullable(userService.findUserByPasswordToken(passwordResetToken));
        log.debug("Is user present: " + theUser.isPresent() + ", User name: " + theUser.get().getName());
        if (theUser.isPresent()) {
            userService.changePassword(theUser.get(), passwordResetRequest.newPassword());
            log.info("Password has been reset successfully");
        }
        log.error("Invalid password reset token");
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestBody PasswordRequestUtil requestUtil){
        User user = userService.findByEmail(requestUtil.email()).get();
        if (!userService.oldPasswordIsValid(user, requestUtil.oldPassword())){
            log.error("Incorrect old password");
            return;
        }
        userService.changePassword(user, requestUtil.newPassword());
        log.info("Password changed successfully");
    }
    private String passwordResetEmailLink(User user, String applicationUrl, String passwordResetToken)
            throws MessagingException, UnsupportedEncodingException {
        String url = applicationUrl + "/api/auth/reset-password?token=" + passwordResetToken;
        eventListener.sendPasswordResetVerificationEmail(url);
        return url;
    }
    public String applicationUrl(HttpServletRequest request){
        System.out.println("http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath());
        return "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();
    }
}
