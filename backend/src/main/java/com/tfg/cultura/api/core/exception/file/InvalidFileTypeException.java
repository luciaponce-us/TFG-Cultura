package com.tfg.cultura.api.core.exception.file;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.Logger;

public class InvalidFileTypeException extends ValidationException {
	public InvalidFileTypeException(Logger logger, String field, String allowedTypes) {
		super(logger, Map.of(field, "El tipo de archivo no es válido (Formatos permitidos: " + allowedTypes + ")"));
	}

}