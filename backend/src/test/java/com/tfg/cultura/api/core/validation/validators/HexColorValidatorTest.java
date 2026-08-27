package com.tfg.cultura.api.core.validation.validators;

import org.junit.jupiter.api.Test;

class HexColorValidatorTest {
	private final HexColorValidator validator = new HexColorValidator();

	private static final String VALID_COLOR = "#FFFFFF";
	private static final String INVALID_COLOR = "FFFFFF";

	@Test
	void should_return_true_for_valid_color() {
		assert validator.isValid(VALID_COLOR, null);
	}

	@Test
	void should_return_true_for_null_color() {
		assert validator.isValid(null, null);
	}

	@Test
	void should_return_true_for_empty_color() {
		assert validator.isValid("", null);
	}

	@Test
	void should_return_false_for_invalid_color() {
		assert !validator.isValid(INVALID_COLOR, null);
	}

}
