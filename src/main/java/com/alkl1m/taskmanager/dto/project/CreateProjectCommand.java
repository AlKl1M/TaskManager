package com.alkl1m.taskmanager.dto.project;

import lombok.Builder;

@Builder
public record CreateProjectCommand(Long id,
                                   String name,
                                   String description) {
}
