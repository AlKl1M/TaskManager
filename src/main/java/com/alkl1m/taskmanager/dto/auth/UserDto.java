package com.alkl1m.taskmanager.dto.auth;

import com.alkl1m.taskmanager.enums.Role;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
}