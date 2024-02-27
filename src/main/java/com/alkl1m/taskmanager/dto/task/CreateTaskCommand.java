package com.alkl1m.taskmanager.dto.task;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record CreateTaskCommand(Long id,
                                String name,
                                String description,
                                Instant deadline,
                                List<String> tags
) {
}