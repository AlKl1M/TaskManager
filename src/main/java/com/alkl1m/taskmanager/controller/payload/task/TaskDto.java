package com.alkl1m.taskmanager.controller.payload.task;

import com.alkl1m.taskmanager.entity.Task;
import com.alkl1m.taskmanager.enums.Status;
import lombok.Builder;

import java.time.Instant;

@Builder
public record TaskDto(Long id,
                      String name,
                      String description,
                      Instant creationDate,
                      Instant completionDate,
                      Instant deadline,
                      Status status,
                      String tags) {
    public static TaskDto from(Task task) {
        return new TaskDto(task.getId(),
                task.getName(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getDoneAt(),
                task.getDeadline(),
                task.getStatus(),
                task.getTags()
        );
    }
}
