package com.alkl1m.taskmanager.controller.payload.task;

import com.alkl1m.taskmanager.enums.Status;

import java.time.Instant;
import java.util.List;

public record CreateBackTaskDto(
    Long id,
    String name,
    String description,
    Instant creationDate,
    Instant completionDate,
    Instant deadline,
    Status status,
    List<String> tags) {
        public static CreateBackTaskDto from(TaskDto taskDto) {
            return new CreateBackTaskDto(taskDto.id(),
                    taskDto.name(),
                    taskDto.description(),
                    taskDto.creationDate(),
                    taskDto.completionDate(),
                    taskDto.deadline(),
                    taskDto.status(),
                    List.of(taskDto.tags().split("&#/!&"))
            );
        }
}
