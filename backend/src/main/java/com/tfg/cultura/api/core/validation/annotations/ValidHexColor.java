package com.tfg.cultura.api.core.validation.annotations;

import com.tfg.cultura.api.core.validation.validators.HexColorValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HexColorValidator.class)
public @interface ValidHexColor {
	String message() default "El color debe ser un código hexadecimal válido";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
