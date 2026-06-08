package com.tfg.cultura.api.core.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tfg.cultura.api.core.validation.validators.ImageFileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
public @interface ValidImage {
    String message() default "Archivo no válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
