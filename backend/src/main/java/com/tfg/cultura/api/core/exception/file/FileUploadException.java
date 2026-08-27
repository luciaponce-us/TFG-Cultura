package com.tfg.cultura.api.core.exception.file;

import com.tfg.cultura.api.core.exception.ApiException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class FileUploadException extends ApiException {
	public FileUploadException(String message) {
		super("Error al subir archivo a Cloudinary: " + message, LoggerFactory.getLogger("appLogger"),
				HttpStatus.SERVICE_UNAVAILABLE);
	}
}
