package com.tfg.cultura.api.core.exception;

import java.util.Map;
import lombok.Getter;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

@Getter
public abstract class FieldException extends ApiException {

	private final Map<String, String> errors;

	public FieldException(Logger logger, HttpStatus status, Map<String, String> errors) {
		super(errors == null || errors.isEmpty()
				? "Errores de validación en los campos del formulario"
				: String.join("; ", errors.values()), logger, status);
		this.errors = errors;
	}
}
