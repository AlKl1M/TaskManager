package com.alkl1m.taskmanager.controller.payload.auth;

public record PasswordRequestUtil(
        String email,
        String oldPassword,
        String newPassword) {
}
