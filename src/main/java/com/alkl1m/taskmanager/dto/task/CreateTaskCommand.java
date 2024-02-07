package com.alkl1m.taskmanager.dto.task;

import java.util.List;

public record CreateTaskCommand(Long id,
                                String name,
                                String description,
                                List<String> tags
) {
}