package com.tfg.cultura.api.users.exception;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.DuplicationException;

public class UserAlreadyExistsException extends DuplicationException {
    public UserAlreadyExistsException(Map<String, String> errors) {
        super(
            LoggerFactory.getLogger("usersLogger"),
            errors
        );
    }
}
