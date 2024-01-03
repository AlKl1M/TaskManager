package com.alkl1m.taskmanager.dto;

import com.alkl1m.taskmanager.enums.UserRole;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private UserRole userRole;
}