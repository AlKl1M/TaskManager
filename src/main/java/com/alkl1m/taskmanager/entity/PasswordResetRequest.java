package com.alkl1m.taskmanager.entity;

public record PasswordResetRequest(
    String email,
    String newPassword,
    String confirmPassword){ }
