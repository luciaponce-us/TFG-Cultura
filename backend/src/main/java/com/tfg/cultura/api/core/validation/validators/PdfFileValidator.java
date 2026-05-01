package com.tfg.cultura.api.core.validation.validators;

import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.core.validation.annotations.ValidPdf;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PdfFileValidator
        implements ConstraintValidator<ValidPdf, MultipartFile> {

    private static final long MAX_MB = 2;
    private static final long MAX_SIZE = MAX_MB * 1024 * 1024;

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        if (file == null || file.isEmpty())
            return true; // opcional

        String contentType = file.getContentType();

        if (contentType == null || !contentType.equals("application/pdf")) {
            return false;
        }

        return file.getSize() <= MAX_SIZE;
    }
}