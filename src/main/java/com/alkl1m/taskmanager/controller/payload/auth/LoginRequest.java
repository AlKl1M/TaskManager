package com.alkl1m.taskmanager.controller.payload.auth;

public record LoginRequest(
        String email,
        String password) {

}
