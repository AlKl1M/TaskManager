package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.auth.*;
import com.alkl1m.taskmanager.entity.*;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.event.RegistrationCompleteEvent;
import com.alkl1m.taskmanager.exception.TokenRefreshException;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.auth.RefreshTokenService;
import com.alkl1m.taskmanager.service.auth.UserDetailsImpl;
import com.alkl1m.taskmanager.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final ApplicationEventPublisher publisher;


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Authenticating user: {}", loginRequest.email());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());
        log.info("User {} authenticated successfully", loginRequest.email());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        Role.USER));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest,
                                          final HttpServletRequest request) {
        if (userRepository.existsByEmail(signupRequest.email())) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }

        if (userRepository.existsByName(signupRequest.name())) {
            return ResponseEntity
                    .badRequest()
                    .build();
        }
        log.info("Registering user: {}", signupRequest.email());
        User user = new User(signupRequest.name(),
                signupRequest.email(),
                encoder.encode(signupRequest.password()),
                Role.USER);
        userRepository.save(user);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        log.info("User {} registered successfully", signupRequest.email());
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody HttpServletRequest request) {
        log.info("Refreshing token...");
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        log.debug("Refresh token: {}", refreshToken);
        if ((refreshToken != null) && (refreshToken.length() > 0)) {
            return refreshTokenService.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                        log.info("Token refreshed successfully!");
                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new MessageResponse("Token is refreshed successfully!"));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken,
                            "Refresh token is not id database!"));
        }
        log.warn("Refresh Token is empty!");
        return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logging out user");
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        refreshTokenService.deleteByUserId(userId);

        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshToken();
        log.info("Cleaned JWT cookies. User signed out");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }

    public String applicationUrl(HttpServletRequest request){
        System.out.println("http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath());
        return "http://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath();
    }
}
