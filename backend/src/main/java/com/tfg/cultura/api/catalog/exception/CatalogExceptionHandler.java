package com.tfg.cultura.api.catalog.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tfg.cultura.api.core.exception.ApiError;
import com.tfg.cultura.api.core.exception.ApiErrorBuilder;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(basePackages = "com.tfg.cultura.api")
@RequiredArgsConstructor
public class CatalogExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger("catalogLogger");
	private final ApiErrorBuilder apiErrorBuilder;

	@ExceptionHandler(CategoryNotFoundException.class)
	public ResponseEntity<ApiError> handleCategoryNotFoundException(CategoryNotFoundException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.NOT_FOUND,
				"Category Not Found",
				logger);
	}

	@ExceptionHandler(ItemNotFoundException.class)
	public ResponseEntity<ApiError> handleBookNotFoundException(ItemNotFoundException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.NOT_FOUND,
				"Item Not Found",
				logger);
	}

	@ExceptionHandler(SamePrequelAndSequelException.class)
	public ResponseEntity<ApiError> handleSamePrequelAndSequelException(SamePrequelAndSequelException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.BAD_REQUEST,
				"Same Prequel and Sequel",
				logger);
	}

	@ExceptionHandler(ItemCannotBeItsOwnSequelException.class)
	public ResponseEntity<ApiError> handleItemCannotBeItsOwnSequelException(ItemCannotBeItsOwnSequelException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.BAD_REQUEST,
				"Item Cannot Be Its Own Sequel",
				logger);
	}

	@ExceptionHandler(ItemCannotBeItsOwnPrequelException.class)
	public ResponseEntity<ApiError> handleItemCannotBeItsOwnPrequelException(ItemCannotBeItsOwnPrequelException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.BAD_REQUEST,
				"Item Cannot Be Its Own Prequel",
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

	@ExceptionHandler(DuplicateItemNumberInSagaException.class)
	public ResponseEntity<ApiError> handleDuplicateItemNumberInSagaException(DuplicateItemNumberInSagaException ex) {
		return apiErrorBuilder.build(
				ex,
				HttpStatus.CONFLICT,
				"Duplicate Item Number in Saga",
				logger);
	}

}