package com.tfg.cultura.api.core.exception;

import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ApiException extends RuntimeException {

	private final Logger logger;
	private final HttpStatus status;

	public ApiException(String message, Logger logger, HttpStatus status) {
		super(message);
		this.logger = logger;
		this.status = status;
	}
}
