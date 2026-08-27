package com.tfg.cultura.api.core.exception.file;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.Logger;

public class InvalidFileSizeException extends ValidationException {
	public InvalidFileSizeException(Logger logger, String field, Integer maxSizeMb) {
		super(logger, Map.of(field, "El tamaño del archivo excede el límite permitido (Máximo: " + maxSizeMb + " MB)"));
	}

}
