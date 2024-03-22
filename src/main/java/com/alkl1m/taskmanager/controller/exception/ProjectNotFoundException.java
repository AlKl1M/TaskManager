package com.alkl1m.taskmanager.controller.exception;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(Long id) {
        super(String.format("Project with id=%d not found", id));
    }

    public static ProjectNotFoundException of(Long id) {
        return new ProjectNotFoundException(id);
    }
}
