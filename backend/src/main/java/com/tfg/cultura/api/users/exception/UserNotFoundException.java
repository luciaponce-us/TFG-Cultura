package com.tfg.cultura.api.users.exception;

import com.tfg.cultura.api.core.exception.NotFoundException;
import org.slf4j.LoggerFactory;

public class UserNotFoundException extends NotFoundException {
	public UserNotFoundException(String message) {
		super(message, LoggerFactory.getLogger("usersLogger"));
	}
}
