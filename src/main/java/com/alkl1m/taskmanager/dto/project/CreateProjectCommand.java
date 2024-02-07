package com.alkl1m.taskmanager.dto.project;

public record CreateProjectCommand(Long id,
                                   String name,
                                   String description) {
}
