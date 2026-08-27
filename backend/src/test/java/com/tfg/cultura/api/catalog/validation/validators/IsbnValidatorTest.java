package com.tfg.cultura.api.catalog.validation.validators;

import org.junit.jupiter.api.Test;

class IsbnValidatorTest {
    private final IsbnValidator validator = new IsbnValidator();

    private static final String[] VALID_ISBN10 = {"0306406152", "043942089X"};
    private static final String[] INVALID_ISBN10 = {"0306406153", "030640615A", "0306406A52"};
    private static final String VALID_ISBN13 = "9780306406157";
    private static final String[] INVALID_ISBN13 = {"9780306406158", "978030640615X", "97803064061A7"};
    private static final String INVALID_LENGTH_ISBN = "030640615";

    @Test
    void should_return_true_for_valid_isbn10() {
        assert validator.isValid(VALID_ISBN10[0], null);
        assert validator.isValid(VALID_ISBN10[1], null);
    }

    @Test
    void should_return_true_for_valid_isbn13() {
        assert validator.isValid(VALID_ISBN13, null);
    }

    @Test
    void should_return_true_for_null_isbn() {
        assert validator.isValid(null, null);
    }

    @Test
    void should_return_true_for_empty_isbn() {
        assert validator.isValid("", null);
    }

    @Test
    void should_return_false_for_invalid_isbn10() {
        assert !validator.isValid(INVALID_ISBN10[0], null);
        assert !validator.isValid(INVALID_ISBN10[1], null);
        assert !validator.isValid(INVALID_ISBN10[2], null);
    }

    @Test
    void should_return_false_for_invalid_isbn13() {
        assert !validator.isValid(INVALID_ISBN13[0], null);
        assert !validator.isValid(INVALID_ISBN13[1], null);
        assert !validator.isValid(INVALID_ISBN13[2], null);
    }

    @Test
    void should_return_false_for_invalid_isbn_length() {
        assert !validator.isValid(INVALID_LENGTH_ISBN, null);
    }
    
}
