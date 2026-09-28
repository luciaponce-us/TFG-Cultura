package com.tfg.cultura.api.core.exception.file;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.Logger;

@SuppressWarnings("java:S110") // This class has a high number of parents, but it is very useful and reusable
public class InvalidFileSizeException extends ValidationException {
	public InvalidFileSizeException(Logger logger, String field, Integer maxSizeMb) {
		super(logger, Map.of(field, "El tamaño del archivo excede el límite permitido (Máximo: " + maxSizeMb + " MB)"));
	}

}
