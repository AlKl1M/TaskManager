package com.alkl1m.taskmanager.controller.exception;

import jakarta.persistence.criteria.CriteriaBuilder;

public class InvalidPasswordResetTokenException extends RuntimeException{
    public InvalidPasswordResetTokenException(){
        super("User try to reset password with invalid password reset token");
    }
    public static InvalidPasswordResetTokenException of(){
        return new InvalidPasswordResetTokenException();
    }
}
