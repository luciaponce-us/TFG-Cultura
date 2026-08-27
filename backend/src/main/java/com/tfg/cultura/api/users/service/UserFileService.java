package com.tfg.cultura.api.users.service;

import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.core.exception.file.FileUploadException;
import com.tfg.cultura.api.core.exception.file.InvalidFileTypeException;
import com.tfg.cultura.api.core.service.FileService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserFileService {

	private final FileService fileService;

	private static final Logger logger = LoggerFactory.getLogger("usersLogger");
	public static final String AVATAR_PLACEHOLDER = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png";
	public static final String PAYMENT_RECEIPT_PLACEHOLDER = "https://www.soundczech.cz/temp/lorem-ipsum.pdf";
	private static final String AVATAR_FOLDER = "cultura/avatars";
	private static final String PAYMENT_FOLDER = "cultura/payment_receipts";

	protected String uploadAvatar(String userId, MultipartFile file) throws FileUploadException {
		return fileService.uploadImage("user", userId, file, AVATAR_FOLDER, AVATAR_PLACEHOLDER, 300, 300, logger,
				"avatar");
	}

	protected String uploadPaymentReceiptPdf(String userId, MultipartFile file) throws FileUploadException {
		validatePaymentReceipt(file);
		return fileService.uploadPdf("payment", userId, file, PAYMENT_FOLDER, PAYMENT_RECEIPT_PLACEHOLDER, logger,
				"paymentReceipt");
	}

	protected void validateAvatar(MultipartFile file) {
		fileService.validateImageSize(file, logger, "avatar");
	}

	protected void validatePaymentReceipt(MultipartFile pdf) {
		if (pdf == null || pdf.isEmpty())
			throw new ValidationException(logger,
					Map.of("paymentReceipt", "El archivo de carta de pago es obligatorio"));
		String contentType = pdf.getContentType();
		if (contentType == null || !contentType.equals("application/pdf")) {
			throw new InvalidFileTypeException(logger, "paymentReceipt", "PDF");
		}
	}

	protected void deleteUserFile(String fileUrl) {
		if (fileUrl != null && !fileUrl.isEmpty()) {
			boolean isPlaceholder = fileUrl.equals(AVATAR_PLACEHOLDER) || fileUrl.equals(PAYMENT_RECEIPT_PLACEHOLDER);
			if (!isPlaceholder) {
				fileService.deleteFile(fileUrl);
			}
		}
	}
}
