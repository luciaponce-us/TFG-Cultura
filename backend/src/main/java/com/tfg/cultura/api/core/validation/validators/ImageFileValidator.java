package com.tfg.cultura.api.core.validation.validators;

import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.core.validation.annotations.ValidImage;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageFileValidator 
        implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final long MAX_MB = 2;
    private static final long MAX_SIZE = MAX_MB * 11048576; // 2MB

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty()) return true; // campo opcional

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return false;
        }

        return file.getSize() <= MAX_SIZE;
    }
}
