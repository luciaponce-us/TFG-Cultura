package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.validation.annotations.ValidYouTubeEmbedUrl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class YouTubeEmbedUrlValidator implements ConstraintValidator<ValidYouTubeEmbedUrl, String> {

	private static final String YOUTUBE_EMBED_URL_REGEX = "^https:\\/\\/www\\.youtube\\.com\\/embed\\/[a-zA-Z0-9_-]+$";

	@Override
	public boolean isValid(String url, ConstraintValidatorContext context) {
		if (url == null || url.isEmpty())
			return true; // campo opcional
		return url.matches(YOUTUBE_EMBED_URL_REGEX);
	}

}
