package com.tfg.cultura.api.core.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public abstract class FieldException extends ApiException {

    private final Map<String, String> errors;

    public FieldException(Logger logger,HttpStatus status, Map<String, String> errors) {
        super(
            errors == null || errors.isEmpty()
                    ? "Errores de validación en los campos del formulario"
                    : String.join("; ", errors.values()),
            logger,
            status);
        this.errors = errors;
    }
}
