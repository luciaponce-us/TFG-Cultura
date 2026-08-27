package com.tfg.cultura.api.users.exception;

import com.tfg.cultura.api.core.exception.UnauthorizedException;

public class RoleModificationNotAllowedException extends UnauthorizedException {
	public RoleModificationNotAllowedException(String message) {
		super(message);
	}
}
