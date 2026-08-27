package com.tfg.cultura.api.users.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.core.exception.file.FileUploadException;
import com.tfg.cultura.api.core.exception.file.InvalidFileSizeException;
import com.tfg.cultura.api.core.exception.file.InvalidFileTypeException;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.users.factory.UserFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

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
		when(fileService.uploadImage(anyString(), anyString(), any(), anyString(), anyString(), anyInt(), anyInt(),
				any(), anyString())).thenReturn("url/avatar.png");

		String result = service.uploadAvatar(userId, AVATAR_FILE);

		assertEquals("url/avatar.png", result);
	}

	@Test
	void should_propagate_avatar_upload_failure() {
		String userId = "123";
		when(fileService.uploadImage(anyString(), anyString(), any(), anyString(), anyString(), anyInt(), anyInt(),
				any(), anyString())).thenThrow(new FileUploadException("error"));

		assertThrows(FileUploadException.class, () -> service.uploadAvatar(userId, AVATAR_FILE));
	}

	// UPLOAD PAYMENT RECEIPT

	@Test
	void should_throw_FileUploadException_when_upload_fails() {
		String userId = "123";
		when(fileService.uploadPdf(anyString(), anyString(), any(), anyString(), anyString(), any(), anyString()))
				.thenThrow(new FileUploadException("Cloud error"));

		FileUploadException exception = assertThrows(FileUploadException.class,
				() -> service.uploadPaymentReceiptPdf(userId, PDF_FILE));

		assertTrue(exception.getMessage().contains("Cloud error"));

		verify(fileService).uploadPdf(anyString(), anyString(), any(), anyString(), anyString(), any(), anyString());
	}

	// VALIDATE AVATAR

	@Test
	void should_throw_exception_when_avatar_is_not_image() {
		doThrow(new InvalidFileTypeException(null, "avatar", "imagen")).when(fileService).validateImageSize(any(),
				any(), anyString());

		assertThrows(InvalidFileTypeException.class, () -> service.validateAvatar(PDF_FILE));
	}

	@Test
	void should_throw_exception_when_avatar_exceeds_max_size() {
		doThrow(new InvalidFileSizeException(null, "avatar", 2)).when(fileService).validateImageSize(any(), any(),
				anyString());

		assertThrows(InvalidFileSizeException.class, () -> service.validateAvatar(file));
	}

	@Test
	void should_pass_when_avatar_is_valid() {
		assertDoesNotThrow(() -> service.validateAvatar(file));
	}

	// VALIDATE PAYMENT RECEIPT

	@Test
	void should_throw_exception_when_pdf_is_null() {
		ValidationException ex = assertThrows(ValidationException.class, () -> service.validatePaymentReceipt(null));

		assertTrue(ex.getMessage().contains("carta de pago"));
	}

	@Test
	void should_throw_exception_when_pdf_is_empty() {
		when(file.isEmpty()).thenReturn(true);

		ValidationException ex = assertThrows(ValidationException.class, () -> service.validatePaymentReceipt(file));

		assertTrue(ex.getMessage().contains("carta de pago"));
	}

	@Test
	void should_throw_exception_when_pdf_is_not_pdf() {
		when(file.isEmpty()).thenReturn(false);
		when(file.getContentType()).thenReturn("image/png");

		InvalidFileTypeException ex = assertThrows(InvalidFileTypeException.class,
				() -> service.validatePaymentReceipt(file));

		assertTrue(ex.getMessage().contains("PDF"));
	}

	@Test
	void should_throw_exception_when_pdf_exceeds_max_size() {
		assertDoesNotThrow(() -> service.validatePaymentReceipt(PDF_FILE));
	}

	@Test
	void should_pass_when_pdf_is_valid() {
		assertDoesNotThrow(() -> service.validatePaymentReceipt(PDF_FILE));
	}

	// DELETE USER FILE

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
