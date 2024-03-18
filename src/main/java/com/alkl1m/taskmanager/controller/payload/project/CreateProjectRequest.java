package com.alkl1m.taskmanager.controller.payload.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotNull(message = "{taskmanager.project.errors.name_is_null}")
        @Size(min = 3, max = 60, message = "{taskmanager.project.errors.name_size_in_invalid}")
        @NotBlank(message = "{taskmanager.project.errors.name_is_blank}")
        String name,
        @NotNull(message = "{taskmanager.project.errors.description_is_null}")
        @Size(min = 3, max = 90, message = "{taskmanager.project.errors.description_size_in_invalid}")
        @NotBlank(message = "{taskmanager.project.errors.description_is_blank}")
        String description) {}
