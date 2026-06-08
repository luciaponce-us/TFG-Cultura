package com.tfg.cultura.api.users.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import com.tfg.cultura.api.users.validation.annotations.ValidPhone;

public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private boolean required;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        this.required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.trim().isEmpty()) {
            return !required;
        }

        String cleaned = phone.replaceAll("[\\s\\-()]", "");
       
        // Permite:
        // +34600123456
        // 600123456
        // 0034600123456
        String regex = "^(\\+\\d{1,3}|00\\d{1,3})?\\d{9}$";

        return cleaned.matches(regex);
    }
    
}