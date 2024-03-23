package com.alkl1m.taskmanager.controller.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(String email){
        super(String.format("User not found with email: %s", email));
    }

    public static UserNotFoundException of(String email){
        return new UserNotFoundException(email);
    }
}
