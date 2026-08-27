package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.validation.annotations.ValidCloudinaryUrl;
import com.tfg.cultura.api.core.validation.enums.ResourceType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CloudinaryUrlValidator implements ConstraintValidator<ValidCloudinaryUrl,String>{
    private ResourceType type = ResourceType.IMAGE;

    @Override
    public void initialize(ValidCloudinaryUrl annotation) {
        this.type = annotation.type();
    }

    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.isEmpty()) return true; // campo opcional
        String resourceType = type.name().toLowerCase();
        String regex = "^https://res\\.cloudinary\\.com/[^/]+/(" + resourceType + ")/upload/.+";
        
        return url.matches(regex);
    }
    
}
