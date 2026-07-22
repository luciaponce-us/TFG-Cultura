package com.tfg.cultura.api.sections.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tfg.cultura.api.core.exception.ApiErrorBuilder;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(basePackages = "com.tfg.cultura.api")
@RequiredArgsConstructor
public class SectionExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");
    private final ApiErrorBuilder apiErrorBuilder;
}
