package com.tfg.cultura.api.core.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger("appLogger");

    private final ApiErrorBuilder apiErrorBuilder;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        ValidationException validationException = new ValidationException(
                logger,
                ex.getBindingResult().getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                            error -> error.getDefaultMessage(),
                            (first, second) -> first)));

        return apiErrorBuilder.build(validationException);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        return apiErrorBuilder.build(ex);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        return apiErrorBuilder.build(ex, HttpStatus.BAD_REQUEST, logger, "Solicitud inválida");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        return apiErrorBuilder.build(ex, HttpStatus.UNAUTHORIZED, logger, ex.getMessage());
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiError> handleDisabledUser(DisabledException ex) {
        return apiErrorBuilder.build(ex, HttpStatus.FORBIDDEN, logger, ex.getMessage());
    }

}
