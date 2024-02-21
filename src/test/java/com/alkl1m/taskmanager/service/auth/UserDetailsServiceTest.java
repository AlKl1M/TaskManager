package com.alkl1m.taskmanager.service.auth;

import com.alkl1m.taskmanager.entity.User;
import com.alkl1m.taskmanager.enums.Role;
import com.alkl1m.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;


    @BeforeEach()
    public void setup() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@test.com")
                .password("123")
                .role(Role.USER)
                .enabled(true)
                .build();
    }
    @AfterEach
    public void tearDown(){
        userRepository.deleteAll();
    }

    @Test
    public void UserDetailsService_LoadUserByEmail_ReturnsUser(){
        String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        assertEquals(userDetails.getUsername(), user.getName());
        assertEquals(userDetails.getPassword(), user.getPassword());
    }

}
