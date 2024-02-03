package com.alkl1m.taskmanager.dto.task;

import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.enums.Status;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record TaskDto(Long id,
                      String name,
                      String description,
                      Instant creationDate,
                      Instant completionDate,
                      Status status,
                      String tags) {
    public static TaskDto from(Task task) {
        return new TaskDto(task.getId(),
                task.getName(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getDoneAt(),
                task.getStatus(),
                task.getTags()
        );
    }
}
