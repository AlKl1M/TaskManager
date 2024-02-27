package com.alkl1m.taskmanager.dto.task;

import java.time.Instant;
import java.util.List;

public record UpdateTaskCommand(
        Long id,
        String name,
        String description,
        Instant deadline,
        List<String> tags
        ) {
}