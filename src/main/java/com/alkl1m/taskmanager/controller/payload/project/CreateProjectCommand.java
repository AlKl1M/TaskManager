package com.alkl1m.taskmanager.controller.payload.project;

import lombok.Builder;

@Builder
public record CreateProjectCommand(
        Long id,
        String name,
        String description) {
}
