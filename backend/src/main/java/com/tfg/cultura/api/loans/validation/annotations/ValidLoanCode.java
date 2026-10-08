package com.tfg.cultura.api.loans.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tfg.cultura.api.loans.validation.validators.LoanCodeValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LoanCodeValidator.class)
public @interface ValidLoanCode {
	String message() default "El código del préstamo no sigue el formato esperado (ejemplo: ABC123)";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
