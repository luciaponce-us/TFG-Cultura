package com.tfg.cultura.api.core.exception;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

	public NotFoundException(String message, Logger logger) {
		super(message, logger, HttpStatus.NOT_FOUND);
	}

}
