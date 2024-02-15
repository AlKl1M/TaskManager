package com.alkl1m.taskmanager.dto.auth;

public record PasswordResetDto(
        String email,
        String password) {
}
