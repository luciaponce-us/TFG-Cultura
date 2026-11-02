package com.tfg.cultura.api.loans.validation.validators;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class LoanCodeValidatorTest {
    private final LoanCodeValidator validator = new LoanCodeValidator();

    @Test
    void should_return_true_for_valid_loan_code() {
        assertTrue(validator.isValid("ABC123", null));
    }

    @Test
    void should_return_true_for_null_loan_code() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void should_return_true_for_empty_loan_code() {
        assertTrue(validator.isValid("", null));
    }

    @Test
    void should_return_false_for_lowercase_letters() {
        assertFalse(validator.isValid("abc123", null));
    }

    @Test
    void should_return_false_for_wrong_number_of_letters() {
        assertFalse(validator.isValid("AB1234", null));
        assertFalse(validator.isValid("ABCD12", null));
    }

    @Test
    void should_return_false_for_wrong_number_of_digits() {
        assertFalse(validator.isValid("ABC12", null));
        assertFalse(validator.isValid("ABC1234", null));
    }

    @Test
    void should_return_false_when_code_contains_non_alphanumeric_characters() {
        assertFalse(validator.isValid("ABC-12", null));
        assertFalse(validator.isValid("ABC 12", null));
    }

    @Test
    void should_return_false_when_letters_and_digits_are_not_in_expected_order() {
        assertFalse(validator.isValid("123ABC", null));
        assertFalse(validator.isValid("AB1C23", null));
    }
}
