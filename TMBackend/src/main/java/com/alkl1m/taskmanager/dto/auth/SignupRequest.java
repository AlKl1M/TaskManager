package com.alkl1m.taskmanager.dto.auth;

public record SignupRequest(String name,
                            String email,
                            String password) {}
