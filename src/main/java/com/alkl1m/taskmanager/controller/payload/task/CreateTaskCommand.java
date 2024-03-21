package com.alkl1m.taskmanager.controller.payload.task;

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