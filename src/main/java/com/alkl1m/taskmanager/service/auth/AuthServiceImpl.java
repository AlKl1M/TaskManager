package com.alkl1m.taskmanager.service.auth;

import com.alkl1m.taskmanager.dto.auth.SignupRequest;
import com.alkl1m.taskmanager.dto.auth.UserDto;
import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.UserRole;
import com.alkl1m.taskmanager.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void createAdminAccount() {
        User adminAccount = userRepository.findByUserRole(UserRole.ADMIN);
        if (adminAccount == null) {
            User user = new User();
            user.setName("admin");
            user.setEmail("admin@test.com");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setUserRole(UserRole.ADMIN);
            userRepository.save(user);
        }
    }

    @Override
    public UserDto createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setName(signupRequest.name());
        user.setEmail(signupRequest.email());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.password()));
        user.setUserRole(UserRole.CLIENT);
        User createdUser = userRepository.save(user);
        UserDto createdUserDto = new UserDto();
        createdUserDto.setId(createdUser.getId());
        createdUserDto.setEmail(createdUser.getName());
        createdUserDto.setEmail(createdUser.getEmail());
        createdUserDto.setUserRole(createdUser.getUserRole());
        return createdUserDto;
    }
}