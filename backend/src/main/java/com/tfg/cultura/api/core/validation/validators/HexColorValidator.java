package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.validation.annotations.ValidHexColor;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HexColorValidator implements ConstraintValidator<ValidHexColor, String>{
    
    @Override
    public boolean isValid(String color, ConstraintValidatorContext context) {
        if (color == null || color.isEmpty()) return true; // campo opcional
        return color.matches("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    }
    
}
