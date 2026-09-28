package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.validation.annotations.ValidUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.URISyntaxException;

public class UrlValidator implements ConstraintValidator<ValidUrl, String> {

	@Override
	public boolean isValid(String url, ConstraintValidatorContext context) {
		if (url == null || url.isEmpty())
			return true; // campo opcional

		try {
			URI parsedUrl = new URI(url);
			String scheme = parsedUrl.getScheme();
			return ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)
					|| "ftp".equalsIgnoreCase(scheme)) && parsedUrl.getRawAuthority() != null
					&& !parsedUrl.getRawAuthority().isEmpty() && parsedUrl.getHost() != null;
		} catch (URISyntaxException exception) {
			return false;
		}
	}

}
