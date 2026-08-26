package com.tfg.cultura.api.core.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

public class ValidationException extends FieldException {

    public ValidationException(Logger logger, Map<String, String> errors) {
        super(logger, HttpStatus.BAD_REQUEST, errors);
    }
}

