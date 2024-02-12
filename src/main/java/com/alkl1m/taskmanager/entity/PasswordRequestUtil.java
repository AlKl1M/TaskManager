package com.alkl1m.taskmanager.entity;

public record PasswordRequestUtil(
        String email,
        String oldPassword,
        String newPassword
) {
}
