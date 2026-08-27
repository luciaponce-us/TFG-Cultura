package com.tfg.cultura.api.core.validation.validators;

import org.junit.jupiter.api.Test;

class UrlValidatorTest {
	private final UrlValidator validator = new UrlValidator();

	private final static String VALID_URL = "https://www.example.com";
	private final static String INVALID_URL = "htp://invalid-url";

	@Test
	void should_return_true_for_valid_url() {
		assert validator.isValid(VALID_URL, null);
	}

	@Test
	void should_return_true_for_null_url() {
		assert validator.isValid(null, null);
	}

	@Test
	void should_return_true_for_empty_url() {
		assert validator.isValid("", null);
	}

	@Test
	void should_return_false_for_invalid_url() {
		assert !validator.isValid(INVALID_URL, null);
	}
}
