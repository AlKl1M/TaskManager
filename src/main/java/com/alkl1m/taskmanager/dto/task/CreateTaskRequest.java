package com.alkl1m.taskmanager.dto.task;

import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;
import java.util.List;

public record CreateTaskRequest(
        @NotEmpty(message = "Name is required")
        String name,
        @NotEmpty(message = "Description is required")
        String description,
        Instant deadline,
        List<String> tags
        ) {}