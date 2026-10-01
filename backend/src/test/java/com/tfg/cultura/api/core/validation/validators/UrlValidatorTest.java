package com.tfg.cultura.api.core.validation.validators;

import org.junit.jupiter.api.Test;

class UrlValidatorTest {
	private final UrlValidator validator = new UrlValidator();

	private static final String VALID_URL = "https://www.example.com";
	private static final String INVALID_URL = "htp://invalid-url";

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

	@Test
	void should_return_false_for_large_malformed_url() {
		String malformedUrl = "https://" + "a".repeat(100_000) + "!";

		assert !validator.isValid(malformedUrl, null);
	}
}
