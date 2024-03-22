package com.alkl1m.taskmanager.controller.exception;

public class InvalidOldPasswordException extends RuntimeException{
    public InvalidOldPasswordException(){
        super("Exception of trying to change password with incorrect old password");
    }

    public static InvalidOldPasswordException of(){
        return new InvalidOldPasswordException();
    }
}
