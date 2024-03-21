package com.alkl1m.taskmanager.controller.payload.project;

public record UpdateProjectCommand(
        Long id,
        String name,
        String description) {
}
