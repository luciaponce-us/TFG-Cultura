package com.tfg.cultura.api.core.validation.annotations;

import com.tfg.cultura.api.core.validation.validators.YouTubeEmbedUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = YouTubeEmbedUrlValidator.class)
public @interface ValidYouTubeEmbedUrl {
	String message() default "La URL de YouTube embebida debe ser válida. Ejemplo: https://www.youtube.com/embed/nsNFOn_4i4E";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
