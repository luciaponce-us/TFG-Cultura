package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.validation.annotations.ValidUrl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

	private static final String URL_REGEX = "^(https?|ftp)://[\\w\\-]+(\\.[\\w\\-]+)*([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-@?^=%&/~\\+#])?$";

	@Override
	public boolean isValid(String url, ConstraintValidatorContext context) {
		if (url == null || url.isEmpty())
			return true; // campo opcional
		return url.matches(URL_REGEX);
	}

}
