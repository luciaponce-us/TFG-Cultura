package com.tfg.cultura.api.catalog.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tfg.cultura.api.catalog.exception.item.*;
import com.tfg.cultura.api.catalog.exception.rolsaga.*;
import com.tfg.cultura.api.catalog.exception.saga.*;
import com.tfg.cultura.api.core.exception.ApiError;
import com.tfg.cultura.api.core.exception.ApiErrorBuilder;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(basePackages = "com.tfg.cultura.api")
@RequiredArgsConstructor
public class CatalogExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger("catalogLogger");
	private final ApiErrorBuilder apiErrorBuilder;

	@ExceptionHandler(ItemNotFoundException.class)
	public ResponseEntity<ApiError> handleBookNotFoundException(ItemNotFoundException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.NOT_FOUND,
				"Item Not Found",
				logger);
	}

	@ExceptionHandler(SagaNotFoundException.class)
	public ResponseEntity<ApiError> handleSagaNotFoundException(SagaNotFoundException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.NOT_FOUND,
				"Saga Not Found",
				logger);
	}

	@ExceptionHandler(RolSagaNotFoundException.class)
	public ResponseEntity<ApiError> handleRolSagaNotFoundException(RolSagaNotFoundException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.NOT_FOUND,
				"Rol Saga Not Found",
				logger);
	}

	@ExceptionHandler(ItemAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleItemAlreadyExistsException(ItemAlreadyExistsException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.CONFLICT,
				"Item Already Exists",
				logger);
	}

	@ExceptionHandler(SagaAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleSagaAlreadyExistsException(SagaAlreadyExistsException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.CONFLICT,
				"Saga Already Exists",
				logger);
	}

	@ExceptionHandler(RolSagaAlreadyExistsException.class)
	public ResponseEntity<ApiError> handleRolSagaAlreadyExistsException(RolSagaAlreadyExistsException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.CONFLICT,
				"Rol Saga Already Exists",
				logger);
		}

}