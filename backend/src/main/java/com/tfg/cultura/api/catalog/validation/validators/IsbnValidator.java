package com.tfg.cultura.api.catalog.validation.validators;

import com.tfg.cultura.api.catalog.validation.annotations.ValidIsbn;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsbnValidator implements ConstraintValidator<ValidIsbn, String> {
    @Override
    public boolean isValid(String isbn, ConstraintValidatorContext context) {
        if (isbn == null || isbn.isEmpty()) {
            return true; // Consider empty ISBN as valid, use @NotBlank for non-empty validation
        }
        return isValidIsbn10(isbn) || isValidIsbn13(isbn);
    }

    private boolean isValidIsbn10(String isbn) {
        if (isbn.length() != 10) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            sum += (c - '0') * (10 - i);
        }
        char lastChar = isbn.charAt(9);
        if (lastChar != 'X' && !Character.isDigit(lastChar)) {
            return false;
        }
        sum += (lastChar == 'X') ? 10 : (lastChar - '0');
        return sum % 11 == 0;
    }

    private boolean isValidIsbn13(String isbn) {
        if (isbn.length() != 13) {
            return false;
        }
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            int digit = c - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return checkDigit == (isbn.charAt(12) - '0');
    }

}
