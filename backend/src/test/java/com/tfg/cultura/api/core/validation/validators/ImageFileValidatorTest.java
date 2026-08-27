package com.tfg.cultura.api.core.validation.validators;

import com.tfg.cultura.api.core.factory.FileFactory;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

class ImageFileValidatorTest {
	private final ImageFileValidator validator = new ImageFileValidator();

	private static final MultipartFile VALID_IMAGE = FileFactory.validImage(1); // 1 MB
	private static final MultipartFile INVALID_TYPE_IMAGE = FileFactory.validPdf(1); // 1 MB
	private static final MultipartFile INVALID_SIZE_IMAGE = FileFactory.validImage(5); // 5 MB
	private static final MultipartFile NULL_CONTENT_TYPE_IMAGE = FileFactory.fileWithoutContentType(2); // 2 MB
	private static final MultipartFile EMPTY_IMAGE = FileFactory.validImage(0); // 0 MB

	@Test
	void should_return_true_for_valid_image() {
		assert validator.isValid(VALID_IMAGE, null);
	}

	@Test
	void should_return_true_for_null_image() {
		assert validator.isValid(null, null);
	}

	@Test
	void should_return_true_for_empty_image() {
		assert validator.isValid(EMPTY_IMAGE, null);
	}

	@Test
	void should_return_false_for_invalid_size_image() {
		assert !validator.isValid(INVALID_SIZE_IMAGE, null);
	}

	@Test
	void should_return_false_for_invalid_type_image() {
		assert !validator.isValid(INVALID_TYPE_IMAGE, null);
	}

	@Test
	void should_return_false_for_null_content_type() {
		assert !validator.isValid(NULL_CONTENT_TYPE_IMAGE, null);
	}

}
