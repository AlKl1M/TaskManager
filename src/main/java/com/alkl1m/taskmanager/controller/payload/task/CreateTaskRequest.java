package com.alkl1m.taskmanager.controller.payload.task;

import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.persistence.Column;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Date;
import java.util.List;


public record CreateTaskRequest(
        @NotNull(message = "{taskmanager.task.errors.name_is_null}")
        @Size(min = 3, max = 60, message = "{taskmanager.task.errors.name_size_in_invalid}")
        @NotBlank(message = "{taskmanager.task.errors.name_is_blank}")
        String name,
        @NotNull(message = "{taskmanager.task.errors.name_is_null}")
        @Size(min = 3, max = 90, message = "{taskmanager.task.errors.name_size_in_invalid}")
        @NotBlank(message = "{taskmanager.task.errors.name_is_blank}")
        String description,

        @FutureOrPresent(message = "{taskmanager.task.errors.deadline_size_in_invalid}")
        Instant deadline,

        @Size(max = 3, message = "{taskmanager.task.errors.tags_size_in_invalid}")
        List<String> tags
        ) {}