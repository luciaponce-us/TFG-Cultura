package com.tfg.cultura.api.core.exception;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message==null ? "Usuario no autorizado" : message,
            LoggerFactory.getLogger("usersLogger"),
            HttpStatus.FORBIDDEN
        );
    }
    
}
