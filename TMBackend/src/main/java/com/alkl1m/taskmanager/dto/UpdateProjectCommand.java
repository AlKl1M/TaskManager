package com.alkl1m.taskmanager.dto;

public record UpdateProjectCommand(
        Long id,
        String name,
        String description) {
}
