package com.tfg.cultura.api.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger("appLogger");

    private final ApiErrorBuilder apiErrorBuilder;

    public GlobalExceptionHandler(ApiErrorBuilder apiErrorBuilder) {
        this.apiErrorBuilder = apiErrorBuilder;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> "Campo " + error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(". "));

        return apiErrorBuilder.build(ex,HttpStatus.BAD_REQUEST,"Errores de validación",logger,message);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ApiError> handleFileUploadException(FileUploadException ex) {
        return apiErrorBuilder.build(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error al subir el archivo", logger);
    }

    @ExceptionHandler(UnathenticatedException.class)
    public ResponseEntity<ApiError> handleUnathenticatedException(UnathenticatedException ex) {
        return apiErrorBuilder.build(ex, HttpStatus.UNAUTHORIZED, "No autenticado", logger);
    }

    @ExceptionHandler(FileDeleteException.class)
    public ResponseEntity<ApiError> handleFileDeleteException(FileDeleteException ex) {
        return apiErrorBuilder.build(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el archivo", logger);
    }

}
