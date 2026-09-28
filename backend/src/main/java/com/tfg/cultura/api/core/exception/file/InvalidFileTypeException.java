package com.tfg.cultura.api.core.exception.file;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.Logger;

@SuppressWarnings("java:S110") // This class has a high number of parents, but it is very useful and reusable
public class InvalidFileTypeException extends ValidationException {
	public InvalidFileTypeException(Logger logger, String field, String allowedTypes) {
		super(logger, Map.of(field, "El tipo de archivo no es válido (Formatos permitidos: " + allowedTypes + ")"));
	}

}