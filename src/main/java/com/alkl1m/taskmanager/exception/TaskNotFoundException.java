package com.alkl1m.taskmanager.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super(String.format("Task with id=%d not found", id));
    }

    public static TaskNotFoundException of(Long id) {
        return new TaskNotFoundException(id);
    }
}
