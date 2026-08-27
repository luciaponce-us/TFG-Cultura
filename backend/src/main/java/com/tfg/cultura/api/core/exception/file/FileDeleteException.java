package com.tfg.cultura.api.core.exception.file;

import com.tfg.cultura.api.core.exception.ApiException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class FileDeleteException extends ApiException {
	public FileDeleteException(String message) {
		super("Error al eliminar archivo de Cloudinary: " + message, LoggerFactory.getLogger("appLogger"),
				HttpStatus.SERVICE_UNAVAILABLE);
	}
}
