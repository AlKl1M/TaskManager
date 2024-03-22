package com.alkl1m.taskmanager.controller.exception;


public class InvalidVerificationTokenException extends RuntimeException{
    public InvalidVerificationTokenException(){
        super("User trying to use invalid verification link to verify account");
    }

    public static InvalidVerificationTokenException of(){
        return new InvalidVerificationTokenException();
    }
}
