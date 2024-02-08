package com.alkl1m.taskmanager.dto.task;

import lombok.Builder;

import java.util.List;

@Builder
public record CreateTaskCommand(Long id,
                                String name,
                                String description,
                                List<String> tags
) {
}