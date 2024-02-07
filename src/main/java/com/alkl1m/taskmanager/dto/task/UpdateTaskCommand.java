package com.alkl1m.taskmanager.dto.task;

import java.util.ArrayList;
import java.util.List;

public record UpdateTaskCommand(
        Long id,
        String name,
        String description,

        List<String> tags
        ) {
}