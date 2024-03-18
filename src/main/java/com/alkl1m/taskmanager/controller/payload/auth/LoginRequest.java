package com.alkl1m.taskmanager.controller.payload.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull(message = "{taskmanager.auth.errors.email_is_null}")
        @NotBlank(message = "{taskmanager.auth.errors.email_is_blank}")
        String email,
        @NotNull(message = "{taskmanager.auth.errors.password_is_null}")
        @NotBlank(message = "{taskmanager.auth.errors.password_is_blank}")
        String password) {

}
