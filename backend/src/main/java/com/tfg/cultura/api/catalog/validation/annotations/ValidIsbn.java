package com.tfg.cultura.api.catalog.validation.annotations;

import com.tfg.cultura.api.catalog.validation.validators.IsbnValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsbnValidator.class)
public @interface ValidIsbn {
	String message() default "El ISBN debe ser un ISBN-10 o ISBN-13 válido";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
