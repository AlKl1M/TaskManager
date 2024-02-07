package com.alkl1m.taskmanager.dto.auth;

import com.alkl1m.taskmanager.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String refreshToken;
    private Long id;
    private String username;
    private String email;
    private Role role;

    public JwtResponse(String accessToken, String refreshToken, Long id, String username, String email, Role role) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
