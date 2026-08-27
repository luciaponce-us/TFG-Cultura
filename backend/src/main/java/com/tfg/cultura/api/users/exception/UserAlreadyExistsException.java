package com.tfg.cultura.api.users.exception;

import com.tfg.cultura.api.core.exception.DuplicationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class UserAlreadyExistsException extends DuplicationException {
	public UserAlreadyExistsException(Map<String, String> errors) {
		super(LoggerFactory.getLogger("usersLogger"), errors);
	}
}
