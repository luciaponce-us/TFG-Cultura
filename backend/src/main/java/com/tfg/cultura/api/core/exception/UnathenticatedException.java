package com.tfg.cultura.api.core.exception;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class UnathenticatedException extends ApiException {
    public UnathenticatedException() {
        this(null);
    }

    public UnathenticatedException(String message) {
        super(
                message == null ? "Usuario no autenticado": message,
                LoggerFactory.getLogger("usersLogger"),
                HttpStatus.UNAUTHORIZED);
    }
}
