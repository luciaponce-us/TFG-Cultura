package com.tfg.cultura.api.core.factory;

import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ExceptionsFactory {
	public static final Logger logger = LoggerFactory.getLogger("test-logger");

	public static final DuplicationException duplicationException(String field) {
		return new DuplicationException(logger, Map.of(field, "Already exists"));
	}

	public static final NotFoundException notFoundException = new NotFoundException("Not found", logger);

	public static FieldException fieldException(HttpStatus status, String field) {
		return new FieldException(logger, null, Map.of(field, "error"));
	}

	public static ValidationException validationException(String field) {
		return new ValidationException(logger, Map.of(field, "error"));
	}
}
