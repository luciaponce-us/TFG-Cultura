package com.tfg.cultura.api.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tfg.cultura.api.core.exception.ApiError;
import com.tfg.cultura.api.core.exception.ApiErrorBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice(basePackages = "com.tfg.cultura.api")
public class UsersExceptionHandler {
    private static final Logger usersLogger = LoggerFactory.getLogger("usersLogger");
    private final ApiErrorBuilder apiErrorBuilder;

    public UsersExceptionHandler(ApiErrorBuilder apiErrorBuilder) {
        this.apiErrorBuilder = apiErrorBuilder;
    }
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.CONFLICT,
            "User Already Exists",
            usersLogger);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.NOT_FOUND,
            "User Not Found",
            usersLogger);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledException(DisabledException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.FORBIDDEN,
            "User Disabled",
            usersLogger);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.UNAUTHORIZED,
            "Unathorized",
            usersLogger);
    }

    @ExceptionHandler(SelfActivationNotAllowedException.class)
    public ResponseEntity<ApiError> handleSelfActivationNotAllowedException(SelfActivationNotAllowedException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.FORBIDDEN,
            "Self Activation Not Allowed",
            usersLogger);
    }

}
