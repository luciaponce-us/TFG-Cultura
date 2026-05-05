package com.tfg.cultura.api.suggestions.exception;

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
public class SuggestionExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("suggestionLogger");
    private final ApiErrorBuilder apiErrorBuilder;

    @ExceptionHandler(SuggestionNotFoundException.class)
    public ResponseEntity<ApiError> handleSuggestionNotFoundException(SuggestionNotFoundException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.NOT_FOUND,
            "Suggestion Not Found",
            log);
    }

    @ExceptionHandler(SuggestionAlreadySupportedException.class)
    public ResponseEntity<ApiError> handleSuggestionAlreadySupportedException(SuggestionAlreadySupportedException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.BAD_REQUEST,
            "Suggestion Already Supported",
            log);
    }

    @ExceptionHandler(SelfSupportSuggestionException.class)
    public ResponseEntity<ApiError> handleSelfSupportSuggestionException(SelfSupportSuggestionException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.BAD_REQUEST,
            "Cant Support Own Suggestion",
            log);
    }

    @ExceptionHandler(SuggestionNotSupportedException.class)
    public ResponseEntity<ApiError> handleSuggestionNotSupportedException(SuggestionNotSupportedException ex) {
        return apiErrorBuilder.build(
            ex,
            HttpStatus.BAD_REQUEST,
            "Cant Stop Supporting Not Supported Suggestion",
            log);
    }
    
}
