package com.tfg.cultura.api.core.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tfg.cultura.api.core.validation.validators.PdfFileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PdfFileValidator.class)
public @interface ValidPdf {

    String message() default "PDF no válido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
