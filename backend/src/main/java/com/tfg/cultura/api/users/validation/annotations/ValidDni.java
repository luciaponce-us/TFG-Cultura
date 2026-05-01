package com.tfg.cultura.api.users.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import com.tfg.cultura.api.users.validation.validators.ValidDniValidator;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidDniValidator.class)
public @interface ValidDni {
    String message() default "El DNI no es válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean required() default true;
}
