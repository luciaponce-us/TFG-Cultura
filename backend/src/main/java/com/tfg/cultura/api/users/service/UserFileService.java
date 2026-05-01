package com.tfg.cultura.api.users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Transformation;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;

@Service
public class UserFileService {

    private final FileService fileService;

    public UserFileService(FileService fileService){
        this.fileService = fileService;
    }

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");
    public static final String AVATAR_PLACEHOLDER = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png";

    String uploadAvatar(String userId, MultipartFile file) {
        try {
            FileUploadRequest request = FileUploadRequest.builder()
                    .file(file)
                    .folder("cultura/avatars")
                    .publicId("user_" + userId)
                    .transformation(new Transformation()
                            .width(300)
                            .height(300)
                            .crop("fill"))
                    .build();

            return fileService.uploadFile(request);
        } catch (Exception ex) {
            logger.error("No se ha podido subir el avatar {} para el usuario con id {}: {}", file.getOriginalFilename(),
                    userId, ex.getMessage());
            return AVATAR_PLACEHOLDER;
        }

    }

    String uploadPaymentReceiptPdf(String userId, MultipartFile file) throws FileUploadException {
        try {
            FileUploadRequest request = FileUploadRequest.builder()
                    .file(file)
                    .folder("cultura/payment_receipts")
                    .publicId("payment_" + userId)
                    .build();

            return fileService.uploadFile(request);
        } catch (Exception ex) {
            logger.error(
                    "No se ha podido subir el PDF {} para el usuario con id {}: {}",
                    file.getOriginalFilename(),
                    userId,
                    ex.getMessage());

            throw new FileUploadException(
                    String.format("Error subiendo PDF '%s' para el usuario '%s'",
                            file.getOriginalFilename(),
                            userId));
        }
    }

    void validateAvatar(MultipartFile avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            String contentType = avatar.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("El archivo de avatar debe ser una imagen");
            }

            long maxMB = 2;
            long maxSize = maxMB * 11048576;

            if (avatar.getSize() > maxSize) {
                throw new IllegalArgumentException("El archivo de avatar no puede superar los 2MB");
            }

        }
    }

    void validatePaymentReceipt(MultipartFile pdf) {
        if (pdf == null || pdf.isEmpty())
            throw new IllegalArgumentException("El archivo de carta de pago es obligatorio");
        String contentType = pdf.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("El archivo de carta de pago debe ser un PDF");
        }
        long maxMB = 2;
        long maxSize = maxMB * 11048576;
        if (pdf.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo de carta de pago no puede superar los 2MB");
        }
    }
}
