package com.tfg.cultura.api.core.exception;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {

    private final Logger logger;
    private final HttpStatus status;

    public ApiException(String message, Logger logger, HttpStatus status) {
        super(message);
        this.logger = logger;
        this.status = status;
    }
}
