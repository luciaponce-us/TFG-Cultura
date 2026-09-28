package com.tfg.cultura.api.core.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@SuppressWarnings("java:S110") // This class has a high number of parents, but it is necessary to have a hierarchy of exceptions
public class DuplicationException extends FieldException {

	public DuplicationException(Logger logger, Map<String, String> errors) {
		super(logger, HttpStatus.CONFLICT, errors);
	}
}
