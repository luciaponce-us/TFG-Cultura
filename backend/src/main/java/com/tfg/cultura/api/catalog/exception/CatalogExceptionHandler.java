package com.tfg.cultura.api.catalog.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tfg.cultura.api.core.exception.ApiError;
import com.tfg.cultura.api.core.exception.ApiErrorBuilder;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(basePackages = "com.tfg.cultura.api")
@RequiredArgsConstructor
public class CatalogExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("catalogLogger");
    private final ApiErrorBuilder apiErrorBuilder;
    
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ApiError> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return apiErrorBuilder.build(
                ex,
                HttpStatus.NOT_FOUND,
                "Category Not Found",
                logger);
        }
}