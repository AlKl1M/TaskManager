package com.alkl1m.taskmanager.dto.project;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;

public record UpdateProjectRequest(
        @NotEmpty(message = "Name is required")
        String name,
        @NotEmpty(message = "Description is required")
        String description)
{}
