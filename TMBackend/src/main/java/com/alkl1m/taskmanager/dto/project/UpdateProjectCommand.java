package com.alkl1m.taskmanager.dto.project;

public record UpdateProjectCommand(
        Long id,
        String name,
        String description) {
}
