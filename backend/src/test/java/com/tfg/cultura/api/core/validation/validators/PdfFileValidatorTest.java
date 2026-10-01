package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.factory.FileFactory;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class PdfFileValidatorTest {
	private final PdfFileValidator validator = new PdfFileValidator();

	private static final MultipartFile VALID_PDF = FileFactory.validPdf(1); // 1 MB
	private static final MultipartFile INVALID_TYPE_PDF = FileFactory.validImage(1); // 1 MB
	private static final MultipartFile INVALID_SIZE_PDF = FileFactory.validPdf(5); // 5 MB
	private static final MultipartFile NULL_CONTENT_TYPE_PDF = FileFactory.fileWithoutContentType(2); // 2 MB

	@Test
	void should_return_true_for_valid_pdf() {
		assert validator.isValid(VALID_PDF, null);
	}

	@Test
	void should_return_true_for_null_pdf() {
		assert validator.isValid(null, null);
	}

	@Test
	void should_return_true_for_empty_pdf() {
		MultipartFile emptyPdf = FileFactory.validPdf(0); // 0 MB
		assert validator.isValid(emptyPdf, null);
	}

	@Test
	void should_return_false_for_invalid_size_pdf() {
		assert !validator.isValid(INVALID_SIZE_PDF, null);
	}

	@Test
	void should_return_false_for_invalid_type_pdf() {
		assert !validator.isValid(INVALID_TYPE_PDF, null);
	}

	@Test
	void should_return_false_for_null_content_type() {
		assert !validator.isValid(NULL_CONTENT_TYPE_PDF, null);
	}

}
