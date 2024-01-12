package com.alkl1m.taskmanager.dto.task;

public record UpdateTaskCommand(
        Long id,
        String name,
        String description) {
}