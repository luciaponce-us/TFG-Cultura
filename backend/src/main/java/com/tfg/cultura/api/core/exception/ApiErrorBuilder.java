package com.tfg.cultura.api.core.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ApiErrorBuilder {

	public ResponseEntity<ApiError> build(Exception ex, HttpStatus status, Logger logger, String message) {

		String finalMessage = (message != null && !message.equals("")) ? message : ex.getMessage();

		ApiError response = ApiError.builder().timestamp(LocalDateTime.now()).status(status.value()).errors(null)
				.message(finalMessage).build();

		logger.warn("HTTP {} - {}: {}", status, ex.getClass().getSimpleName(), ex.getMessage());

		return new ResponseEntity<>(response, status);
	}

	public ResponseEntity<ApiError> build(Exception ex, HttpStatus status, Logger logger) {
		return build(ex, status, logger, status.getReasonPhrase());
	}

	public ResponseEntity<ApiError> build(ApiException ex) {
		ApiError response = new ApiError(ex);

		ex.getLogger().warn("HTTP {} - {}: {}", ex.getStatus(), ex.getClass().getSimpleName(), ex.getMessage());
		return new ResponseEntity<>(response, ex.getStatus());
	}

}
