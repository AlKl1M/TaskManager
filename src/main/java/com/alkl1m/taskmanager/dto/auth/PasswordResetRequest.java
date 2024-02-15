package com.alkl1m.taskmanager.dto.auth;

public record PasswordResetRequest(
    String email,
    String newPassword,
    String confirmPassword){ }
