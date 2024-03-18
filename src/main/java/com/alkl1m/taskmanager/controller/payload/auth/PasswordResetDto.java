package com.alkl1m.taskmanager.controller.payload.auth;

public record PasswordResetDto(
        String email,
        String password) {
}
