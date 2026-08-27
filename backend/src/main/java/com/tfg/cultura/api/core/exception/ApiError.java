package com.tfg.cultura.api.core.exception;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiError {
	private LocalDateTime timestamp;
	private int status;
	private Map<String, String> errors;
	private String message;

	public ApiError(ApiException exception) {
		this.timestamp = LocalDateTime.now();
		this.status = exception.getStatus().value();
		this.errors = exception instanceof FieldException fieldException ? fieldException.getErrors() : null;
		this.message = exception.getMessage();
	}

	public ApiError(FieldException exception) {
		this.timestamp = LocalDateTime.now();
		this.status = exception.getStatus().value();
		this.errors = exception.getErrors();
		this.message = exception.getMessage();
	}
}
