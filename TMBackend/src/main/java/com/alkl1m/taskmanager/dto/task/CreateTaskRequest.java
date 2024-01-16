package com.alkl1m.taskmanager.dto.task;

import jakarta.validation.constraints.NotEmpty;

public record CreateTaskRequest(
        @NotEmpty(message = "Name is required")
        String name,
        @NotEmpty(message = "Description is required")
        String description) {}