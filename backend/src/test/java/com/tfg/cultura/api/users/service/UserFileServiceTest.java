package com.tfg.cultura.api.users.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.users.factory.UserFactory;

@ExtendWith(MockitoExtension.class)
class UserFileServiceTest {

    @Mock
    private MultipartFile file;

    @Mock
    private FileService fileService;

    @InjectMocks
    private UserFileService service;

    private static final MockMultipartFile PDF_FILE = UserFactory.valid_payment_receipt_file();
    private static final MockMultipartFile AVATAR_FILE = UserFactory.valid_avatar_file();

    // UPLOAD AVATAR

    @Test
    void should_upload_avatar_successfully() {
        String userId = "123";
        when(fileService.uploadFile(any())).thenReturn("url/avatar.png");

        String result = service.uploadAvatar(userId, AVATAR_FILE);

        assertEquals("url/avatar.png", result);
    }

    @Test
    void should_return_placeholder_when_upload_avatar_fails() {
        String userId = "123";
        when(fileService.uploadFile(any()))
                .thenThrow(new RuntimeException("error"));

        String result = service.uploadAvatar(userId, AVATAR_FILE);

        assertEquals(UserFileService.AVATAR_PLACEHOLDER, result);
    }

    // UPLOAD PAYMENT RECEIPT

    @Test
    void should_throw_FileUploadException_when_upload_fails() {
        String userId = "123";
        when(fileService.uploadFile(any()))
                .thenThrow(new RuntimeException("Cloud error"));

        FileUploadException exception = assertThrows(
                FileUploadException.class,
                () -> service.uploadPaymentReceiptPdf(userId, PDF_FILE));

        assertTrue(exception.getMessage().contains("Error subiendo PDF"));
        assertTrue(exception.getMessage().contains(PDF_FILE.getOriginalFilename()));

        verify(fileService).uploadFile(any());
    }

    // VALIDATE AVATAR

    @Test
    void should_throw_exception_when_avatar_is_not_image() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateAvatar(PDF_FILE));

        assertEquals("El archivo de avatar debe ser una imagen", ex.getMessage());
    }

    @Test
    void should_throw_exception_when_avatar_exceeds_max_size() {
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(999999999L);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validateAvatar(file));

        assertEquals("El archivo de avatar no puede superar los 2MB", ex.getMessage());
    }

    @Test
    void should_pass_when_avatar_is_valid() {
        when(file.getContentType()).thenReturn("image/png");
        when(file.getSize()).thenReturn(1024L);

        assertDoesNotThrow(() -> service.validateAvatar(file));
    }

    // VALIDATE PAYMENT RECEIPT

    @Test
    void should_throw_exception_when_pdf_is_null() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePaymentReceipt(null));

        assertEquals("El archivo de carta de pago es obligatorio", ex.getMessage());
    }

    @Test
    void should_throw_exception_when_pdf_is_empty() {
        when(file.isEmpty()).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePaymentReceipt(file));

        assertEquals("El archivo de carta de pago es obligatorio", ex.getMessage());
    }

    @Test
    void should_throw_exception_when_pdf_is_not_pdf() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/png");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePaymentReceipt(file));

        assertEquals("El archivo de carta de pago debe ser un PDF", ex.getMessage());
    }

    @Test
    void should_throw_exception_when_pdf_exceeds_max_size() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(999999999L);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.validatePaymentReceipt(file));

        assertEquals("El archivo de carta de pago no puede superar los 2MB", ex.getMessage());
    }

    @Test
    void should_pass_when_pdf_is_valid() {
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("application/pdf");
        when(file.getSize()).thenReturn(1024L);

        assertDoesNotThrow(() -> service.validatePaymentReceipt(file));
    }

    // DELETE USER FILES

    @Test
    void should_delete_files_successfully() {
        String avatarUrl = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png";
        String paymentReceiptUrl = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/payment_receipt.pdf";

        assertDoesNotThrow(() -> service.deleteUserFiles(avatarUrl, paymentReceiptUrl));

        verify(fileService).deleteFile(paymentReceiptUrl);
    }

    @Test
    void should_delete_payment_receipt_when_avatar_is_placeholder() {
        String avatarUrl = UserFileService.AVATAR_PLACEHOLDER;
        String paymentReceiptUrl = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/payment_receipt.pdf";

        assertDoesNotThrow(() -> service.deleteUserFiles(avatarUrl, paymentReceiptUrl));

        verify(fileService).deleteFile(paymentReceiptUrl);
    }

    @Test
    void should_delete_avatar_when_not_placeholder() {
        String avatarUrl = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/user_avatar.png";
        String paymentReceiptUrl = UserFileService.PAYMENT_RECEIPT_PLACEHOLDER;

        assertDoesNotThrow(() -> service.deleteUserFiles(avatarUrl, paymentReceiptUrl));

        verify(fileService).deleteFile(avatarUrl);
        verify(fileService, never()).deleteFile(paymentReceiptUrl);
    }

    @Test
    void should_not_delete_payment_receipt_when_placeholder() {
        String avatarUrl = UserFileService.AVATAR_PLACEHOLDER;
        String paymentReceiptUrl = UserFileService.PAYMENT_RECEIPT_PLACEHOLDER;

        assertDoesNotThrow(() -> service.deleteUserFiles(avatarUrl, paymentReceiptUrl));

        verify(fileService, never()).deleteFile(paymentReceiptUrl);
    }

    // DELETE SINGLE USER FILE

    @Test
    void should_delete_user_file_when_valid_and_not_placeholder() {
        String fileUrl = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/user_avatar.png";

        assertDoesNotThrow(() -> service.deleteUserFile(fileUrl));

        verify(fileService).deleteFile(fileUrl);
    }

    @Test
    void should_not_delete_user_file_when_null_or_empty_or_placeholder() {
        assertDoesNotThrow(() -> service.deleteUserFile(null));
        assertDoesNotThrow(() -> service.deleteUserFile(""));
        assertDoesNotThrow(() -> service.deleteUserFile(UserFileService.AVATAR_PLACEHOLDER));

        verify(fileService, never()).deleteFile(any());
    }

}
