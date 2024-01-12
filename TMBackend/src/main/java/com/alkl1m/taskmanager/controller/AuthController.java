package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.dto.auth.AuthenticationRequest;
import com.alkl1m.taskmanager.dto.auth.AuthenticationResponse;
import com.alkl1m.taskmanager.dto.auth.SignupRequest;
import com.alkl1m.taskmanager.dto.auth.UserDto;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.repository.UserRepository;
import com.alkl1m.taskmanager.service.auth.AuthService;
import com.alkl1m.taskmanager.service.jwt.UserDetailsServiceImpl;
import com.alkl1m.taskmanager.util.JwtUserDetails;
import com.alkl1m.taskmanager.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        UserDto createdUserDto = authService.createUser(signupRequest);
        if (createdUserDto == null) {
            return new ResponseEntity<>("User not created. Come again later", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createdUserDto, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest, HttpServletResponse response) throws IOException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.email(), authenticationRequest.password()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username of password");
        } catch (DisabledException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not active");
            return null;
        }
        final JwtUserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.email());
        final String jwt = jwtUtil.generateToken(userDetails);
        Optional<User> optionalUser = userRepository.findFirstByEmail(userDetails.getUsername());

        return optionalUser.map(user -> new AuthenticationResponse(jwt,
                user.getUserRole(),
                user.getId())).orElse(null);
    }
}
