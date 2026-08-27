package com.tfg.cultura.api.core.validation.validators;

import org.junit.jupiter.api.Test;

class YouTubeEmbedUrlValidatorTest {
    private final YouTubeEmbedUrlValidator validator = new YouTubeEmbedUrlValidator();

    private final static String VALID_URL = "https://www.youtube.com/embed/dQw4w9WgXcQ";
    private final static String INVALID_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    @Test
    void should_return_true_for_valid_youtube_embed_url() {
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
    void should_return_false_for_invalid_youtube_embed_url() {
        assert !validator.isValid(INVALID_URL, null);
    }
}
