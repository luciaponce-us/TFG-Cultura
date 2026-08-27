package com.tfg.cultura.api.core.validation.annotations;

import com.tfg.cultura.api.core.validation.enums.ResourceType;
import com.tfg.cultura.api.core.validation.validators.CloudinaryUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CloudinaryUrlValidator.class)
public @interface ValidCloudinaryUrl {
	String message() default "URL de archivo de Cloudinary no válida";
	Class<?>[] groups() default {};
	ResourceType type() default ResourceType.IMAGE;
	Class<? extends Payload>[] payload() default {};
}
