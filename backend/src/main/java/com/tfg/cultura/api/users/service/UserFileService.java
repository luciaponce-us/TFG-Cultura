package com.tfg.cultura.api.users.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserFileService {

    private final FileService fileService;

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");
    public static final String AVATAR_PLACEHOLDER = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png";
    public static final String PAYMENT_RECEIPT_PLACEHOLDER = "https://www.soundczech.cz/temp/lorem-ipsum.pdf";
    private static final String AVATAR_FOLDER = "cultura/avatars";
    private static final String PAYMENT_FOLDER = "cultura/payment_receipts";

    String uploadAvatar(String userId, MultipartFile file) {
        try {
            MultipartFile resizedFile = fileService.resizeImage(file, 300, 300);
            FileUploadRequest request = FileUploadRequest.builder()
                    .file(resizedFile)
                    .folder(AVATAR_FOLDER)
                    .publicId("user_" + userId)
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
                    .folder(PAYMENT_FOLDER)
                    .publicId("payment_" + userId)
                    .resourceType("raw")
                    .build();
            String pdfUrl = fileService.uploadFile(request);
            logger.info("Se ha subido el PDF {} para el usuario con id {}", pdfUrl, userId);

            return pdfUrl;
        } catch (Exception ex) {
            logger.error(
                    "No se ha podido subir el PDF {} para el usuario con id {}: {}",
                    file.getOriginalFilename(),
                    userId,
                    ex.getMessage());

            throw new FileUploadException(
                    String.format("Error subiendo PDF '%s'", file.getOriginalFilename()));
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

    void deleteUserFiles(String avatarUrl, String paymentReceiptUrl) {
        if (!avatarUrl.equals(AVATAR_PLACEHOLDER)) {
            fileService.deleteFile(avatarUrl);
        }
        if(!paymentReceiptUrl.equals(PAYMENT_RECEIPT_PLACEHOLDER)) {
            fileService.deleteFile(paymentReceiptUrl);
        }
    }

    void deleteUserFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty() && !fileUrl.equals(AVATAR_PLACEHOLDER)) {
            fileService.deleteFile(fileUrl);
        }
    }
}
