package com.alkl1m.taskmanager.dto;

public record UpdateTaskCommand(
        Long id,
        String name,
        String description) {
}