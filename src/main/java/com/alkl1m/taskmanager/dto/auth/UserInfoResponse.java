package com.alkl1m.taskmanager.dto.auth;

import com.alkl1m.taskmanager.enums.Role;

public record UserInfoResponse(Long id,
                               String name,
                               String email,
                               Role role) {
}
