package com.alkl1m.taskmanager.controller.payload.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PasswordResetRequestDto(
        @NotNull(message = "{taskmanager.auth.errors.email_is_null}")
        @Size(min = 3, max = 60, message = "{taskmanager.auth.errors.email_size_is_invalid}")
        @NotBlank(message = "{taskmanager.auth.errors.email_is_blank}")
        String email) {

}
