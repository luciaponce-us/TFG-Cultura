package com.tfg.cultura.api.sections.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tfg.cultura.api.core.exception.ApiError;
import com.tfg.cultura.api.core.exception.ApiErrorBuilder;

import lombok.RequiredArgsConstructor;

@RestControllerAdvice(basePackages = "com.tfg.cultura.api")
@RequiredArgsConstructor
public class SectionExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");
    private final ApiErrorBuilder apiErrorBuilder;

    @ExceptionHandler(SectionAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleSectionAlreadyExistsException(SectionAlreadyExistsException ex) {
        return apiErrorBuilder.build(
                ex,
                HttpStatus.CONFLICT,
                "Section Already Exists",
                logger);
    }

    @ExceptionHandler(InvalidManagerRoleException.class)
    public ResponseEntity<ApiError> handleInvalidManagerRoleException(InvalidManagerRoleException ex) {
        return apiErrorBuilder.build(
                ex,
                HttpStatus.BAD_REQUEST,
                "Invalid Manager Role",
                logger);
    }

    @ExceptionHandler(InvalidCollaboratorRoleException.class)
    public ResponseEntity<ApiError> handleInvalidCollaboratorRoleException(InvalidCollaboratorRoleException ex) {
        return apiErrorBuilder.build(
                ex,
                HttpStatus.BAD_REQUEST,
                "Invalid Collaborator Role",
                logger);
    }

    @ExceptionHandler(ManagerAlreadyAssignedException.class)
    public ResponseEntity<ApiError> handleManagerAlreadyAssignedException(ManagerAlreadyAssignedException ex) {
        return apiErrorBuilder.build(
                ex,
                HttpStatus.CONFLICT,
                "Manager Already Assigned to Another Section",
                logger);
    }

    @ExceptionHandler(CollaboratorAlreadyAssignedException.class)
    public ResponseEntity<ApiError> handleCollaboratorAlreadyAssignedException(
            CollaboratorAlreadyAssignedException ex) {
        return apiErrorBuilder.build(
                ex,
                HttpStatus.CONFLICT,
                "Collaborator Already Assigned to Another Section",
                logger);
    }

}
