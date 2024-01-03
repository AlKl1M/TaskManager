package com.alkl1m.taskmanager.dto;

import com.alkl1m.taskmanager.enums.UserRole;
import lombok.Data;

@Data
public class AuthenticationResponse {
    private String jwt;
    private UserRole userRole;
    private Long userId;
}
