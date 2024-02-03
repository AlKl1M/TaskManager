package com.alkl1m.taskmanager.dto.task;

import jakarta.validation.constraints.NotEmpty;

import java.util.ArrayList;

public record CreateTaskRequest(
        @NotEmpty(message = "Name is required")
        String name,
        @NotEmpty(message = "Description is required")
        String description,

        ArrayList<String> tags
        ) {}