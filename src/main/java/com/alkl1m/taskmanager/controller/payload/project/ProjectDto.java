package com.alkl1m.taskmanager.controller.payload.project;

import com.alkl1m.taskmanager.entity.Project;
import com.alkl1m.taskmanager.enums.Status;

import java.time.Instant;

public record ProjectDto(Long id,
                         String name,
                         String description,
                         Instant createdAt,
                         Instant doneAt,
                         Status status) {
    public static ProjectDto from(Project project) {
        return new ProjectDto(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getDoneAt(),
                project.getStatus()
        );
    }
}
