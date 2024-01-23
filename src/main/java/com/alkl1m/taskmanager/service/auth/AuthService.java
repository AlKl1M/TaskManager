package com.alkl1m.taskmanager.service.auth;

import com.alkl1m.taskmanager.dto.auth.SignupRequest;
import com.alkl1m.taskmanager.dto.auth.UserDto;

public interface AuthService {
    UserDto createUser(SignupRequest signupRequest);
}
