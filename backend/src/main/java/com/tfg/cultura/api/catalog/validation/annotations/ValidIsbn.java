package com.tfg.cultura.api.catalog.validation.annotations;

import java.lang.annotation.Target;

import com.tfg.cultura.api.catalog.validation.validators.IsbnValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsbnValidator.class)
public @interface ValidIsbn {
    String message() default "El ISBN debe ser un ISBN-10 o ISBN-13 válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
