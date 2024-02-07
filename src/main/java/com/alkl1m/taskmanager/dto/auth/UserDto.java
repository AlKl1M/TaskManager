package com.alkl1m.taskmanager.dto.auth;

import com.alkl1m.taskmanager.enums.Role;

public record UserDto(Long id,
                      String name,
                      String email,
                      String password,
                      Role role) {
}