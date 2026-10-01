package com.tfg.cultura.api.users.validation.annotations;

import com.tfg.cultura.api.users.validation.validators.ValidPhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneValidator.class)
public @interface ValidPhone {
	String message() default "El teléfono no es válido";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	boolean required() default true;
}
