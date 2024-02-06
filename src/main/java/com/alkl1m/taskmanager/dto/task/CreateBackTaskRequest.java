package com.alkl1m.taskmanager.dto.task;

import com.alkl1m.taskmanager.enums.Status;

import java.time.Instant;
import java.util.List;

public record CreateBackTaskRequest(
    Long id,
    String name,
    String description,
    Instant creationDate,
    Instant completionDate,
    Status status,
    List<String> tags) {
        public static CreateBackTaskRequest from(TaskDto taskDto) {
            return new CreateBackTaskRequest(taskDto.id(),
                    taskDto.name(),
                    taskDto.description(),
                    taskDto.creationDate(),
                    taskDto.completionDate(),
                    taskDto.status(),
                    List.of(taskDto.tags().split("&#/!&"))
            );
        }
}
