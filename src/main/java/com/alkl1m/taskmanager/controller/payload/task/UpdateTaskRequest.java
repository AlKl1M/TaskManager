package com.alkl1m.taskmanager.controller.payload.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;

public record UpdateTaskRequest(
        @NotNull(message = "{taskmanager.task.errors.name_is_null}")
        @Size(min = 3, max = 60, message = "{taskmanager.task.errors.name_size_in_invalid}")
        @NotBlank(message = "{taskmanager.task.errors.name_is_blank}")
        String name,
        @NotNull(message = "{taskmanager.task.errors.name_is_null}")
        @Size(min = 3, max = 90, message = "{taskmanager.task.errors.name_size_in_invalid}")
        @NotBlank(message = "{taskmanager.task.errors.name_is_blank}")
        String description,
        Instant deadline,
        List<String> tags
        ) {
}