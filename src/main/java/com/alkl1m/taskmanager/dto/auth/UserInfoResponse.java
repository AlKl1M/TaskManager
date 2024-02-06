package com.alkl1m.taskmanager.dto.auth;

import com.alkl1m.taskmanager.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserInfoResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
}
