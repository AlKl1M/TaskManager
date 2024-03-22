package com.alkl1m.taskmanager.controller;

import com.alkl1m.taskmanager.controller.exception.InvalidOldPasswordException;
import com.alkl1m.taskmanager.controller.exception.InvalidPasswordResetTokenException;
import com.alkl1m.taskmanager.controller.exception.InvalidVerificationTokenException;
import com.alkl1m.taskmanager.controller.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Locale;

@ControllerAdvice
@RequiredArgsConstructor
public class BadRequestControllerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException exception, Locale locale) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST,
                        this.messageSource.getMessage("errors.400.title", new Object[0],
                                "errors.400.title", locale));
        problemDetail.setProperty("errors",
                exception.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .toList());

        return ResponseEntity.badRequest()
                .body(problemDetail);
    }

    @ExceptionHandler({InvalidVerificationTokenException.class, InvalidPasswordResetTokenException.class,
            InvalidOldPasswordException.class, UserNotFoundException.class})
    public ResponseEntity<ProblemDetail> handleInvalidVerificationTokenException(
            RuntimeException exception) {
        ProblemDetail problemDetail = ProblemDetail
                .forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperty("Error",
                exception.getMessage());
        return ResponseEntity.badRequest()
                .body(problemDetail);
    }
}