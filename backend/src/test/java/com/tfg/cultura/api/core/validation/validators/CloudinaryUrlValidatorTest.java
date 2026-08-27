package com.tfg.cultura.api.core.validation.validators;

import org.junit.jupiter.api.Test;

class CloudinaryUrlValidatorTest {
    private final CloudinaryUrlValidator validator = new CloudinaryUrlValidator();

    private static final String VALID_URL = "https://res.cloudinary.com/demo/image/upload/sample.jpg";
    private static final String INVALID_URL = "https://example.com/sample.jpg";

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