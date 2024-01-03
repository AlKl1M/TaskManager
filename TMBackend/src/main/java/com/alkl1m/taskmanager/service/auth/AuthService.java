package com.alkl1m.taskmanager.service.auth;

import com.alkl1m.taskmanager.dto.SignupRequest;
import com.alkl1m.taskmanager.dto.UserDto;

public interface AuthService {
    UserDto createUser(SignupRequest signupRequest);
}
