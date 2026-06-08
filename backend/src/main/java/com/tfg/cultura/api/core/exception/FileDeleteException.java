package com.tfg.cultura.api.core.exception;

public class FileDeleteException extends RuntimeException {
    public FileDeleteException(String message) {
        super("Error al eliminar archivo de Cloudinary: " + message);
    }
}
