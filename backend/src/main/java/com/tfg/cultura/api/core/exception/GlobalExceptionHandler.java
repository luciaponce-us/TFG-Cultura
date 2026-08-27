package com.tfg.cultura.api.core.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger("appLogger");

	private final ApiErrorBuilder apiErrorBuilder;

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex) {
		ValidationException validationException = new ValidationException(logger,
				ex.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(error -> error.getField(),
						error -> error.getDefaultMessage(), (first, second) -> first)));

		return apiErrorBuilder.build(validationException);
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ApiError> handleMethodValidationException(HandlerMethodValidationException ex) {
		Map<String, String> errors = new LinkedHashMap<>();

		ex.getParameterValidationResults().forEach(result -> {
			if (result instanceof Errors fieldErrors) {
				fieldErrors.getFieldErrors()
						.forEach(error -> errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
			} else {
				errors.putIfAbsent(result.getMethodParameter().getParameterName(), result.getResolvableErrors().stream()
						.map(error -> error.getDefaultMessage()).findFirst().orElse("Valor no válido"));
			}
		});

		ValidationException validationException = new ValidationException(logger, errors);

		return apiErrorBuilder.build(validationException);
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiError> handleApiException(ApiException ex) {
		return apiErrorBuilder.build(ex);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex) {
		return apiErrorBuilder.build(ex, HttpStatus.BAD_REQUEST, logger, "Solicitud inválida");
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
		return apiErrorBuilder.build(ex, HttpStatus.UNAUTHORIZED, logger, ex.getMessage());
	}

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<ApiError> handleDisabledUser(DisabledException ex) {
		return apiErrorBuilder.build(ex, HttpStatus.FORBIDDEN, logger, ex.getMessage());
	}

}
