package com.alkl1m.taskmanager.exception;

public class UnauthorizedAccessException extends RuntimeException {
    public UnauthorizedAccessException() {
        super("User is not authorized to use this entity");
    }
}
