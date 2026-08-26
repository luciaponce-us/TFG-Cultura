package com.tfg.cultura.api.users.exception;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String message) {
        super(message, LoggerFactory.getLogger("usersLogger"));
    }
}
