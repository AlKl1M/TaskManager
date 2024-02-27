package com.alkl1m.taskmanager.dto.auth;

public record PasswordRequestUtil(
        String email,
        String oldPassword,
        String newPassword) {
}
