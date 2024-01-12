package com.alkl1m.taskmanager.dto.project;

import jakarta.validation.constraints.NotEmpty;

public record CreateProjectRequest(
        @NotEmpty(message = "Name is required")
        String name,
        @NotEmpty(message = "Description is required")
        String description) {}
