package com.tfg.cultura.api.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import com.tfg.cultura.api.core.exception.FileDeleteException;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.model.CustomMultipartFile;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;

public class FileServiceTest {

	@BeforeAll
	static void configureHeadless() {
		System.setProperty("java.awt.headless", "true");
	}

	private Cloudinary cloudinary;

	private Uploader uploader;

	private FileService fileService;

	@BeforeEach
	void setUp() {
		cloudinary = Mockito.mock(Cloudinary.class);
		uploader = Mockito.mock(Uploader.class);
		fileService = new FileService(cloudinary);
	}

	@Test
	void should_return_secure_url_when_upload_file() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"photo.jpg",
				"image/jpeg",
				"content".getBytes(StandardCharsets.UTF_8));

		FileUploadRequest request = FileUploadRequest.builder()
				.file(file)
				.folder("users")
				.publicId("user-123")
				.overwrite(false)
				.resourceType("image")
				.build();

		Map<String, Object> uploadResult = new HashMap<>();
		uploadResult.put("secure_url", "https://cdn.example.com/file.png");

		when(cloudinary.uploader()).thenReturn(uploader);
		when(uploader.upload(eq(file.getBytes()), any(Map.class))).thenReturn(uploadResult);

		String result = fileService.uploadFile(request);

		assertEquals("https://cdn.example.com/file.png", result);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Map<String, Object>> optionsCaptor =
				ArgumentCaptor.forClass((Class<Map<String, Object>>) (Class<?>) Map.class);
		verify(uploader).upload(eq(file.getBytes()), optionsCaptor.capture());

		Map<String, Object> options = optionsCaptor.getValue();
		assertEquals("users", options.get("folder"));
		assertEquals("image", options.get("resource_type"));
		assertEquals("upload", options.get("type"));
		assertEquals(false, options.get("overwrite"));
		assertEquals("user-123", options.get("public_id"));
	}

	@Test
	void should_throw_exception_when_upload_file_fails() throws Exception {
		MockMultipartFile file = new MockMultipartFile(
				"file",
				"photo.jpg",
				"image/jpeg",
				"content".getBytes(StandardCharsets.UTF_8));

		FileUploadRequest request = FileUploadRequest.builder()
				.file(file)
				.folder("users")
				.resourceType("image")
				.build();

		when(cloudinary.uploader()).thenReturn(uploader);
		when(uploader.upload(eq(file.getBytes()), any(Map.class)))
				.thenThrow(new RuntimeException("boom"));

		assertThrows(FileUploadException.class, () -> fileService.uploadFile(request));
	}

	@Test
	void should_return_png_multipart_file_when_resize_image() throws Exception {
		byte[] imageBytes = loadExampleImageBytes();
		MockMultipartFile file = new MockMultipartFile(
				"file",
			"example.png",
			"image/png",
				imageBytes);

		MultipartFile result = fileService.resizeImage(file, 64, 64);

		assertNotNull(result);
		assertEquals("image/png", result.getContentType());
		assertEquals("example.png", result.getOriginalFilename());
		assertEquals(true, result.getBytes().length > 0);
	}

	@Test
	void should_return_default_name_when_resize_image_with_null_name() throws Exception {
		byte[] imageBytes = loadExampleImageBytes();
		MultipartFile file = new CustomMultipartFile(
				imageBytes,
				"file",
				null,
				"image/png");

		MultipartFile result = fileService.resizeImage(file, 64, 64);

		assertNotNull(result);
		assertEquals("image.png", result.getOriginalFilename());
	}

	@Test
	void should_call_destroy_when_delete_file() throws Exception {
		when(cloudinary.uploader()).thenReturn(uploader);

		String url = "https://res.cloudinary.com/demo/image/upload/v1234567890/users/user-1.png";
		fileService.deleteFile(url);

		verify(uploader).destroy(eq("users/user-1"), any(Map.class));
	}

	@Test
	void should_throw_exception_when_delete_file_fails() throws Exception {
		when(cloudinary.uploader()).thenReturn(uploader);
		when(uploader.destroy(eq("users/user-1"), any(Map.class)))
				.thenThrow(new RuntimeException("boom"));

		String url = "https://res.cloudinary.com/demo/image/upload/users/user-1.png";

		assertThrows(FileDeleteException.class, () -> fileService.deleteFile(url));
	}

	@Test
	void should_allow_dot_index_zero_when_extracting_public_id() throws Exception {
		when(cloudinary.uploader()).thenReturn(uploader);

		String url = "https://res.cloudinary.com/demo/image/upload/.png";
		fileService.deleteFile(url);

		verify(uploader).destroy(eq(".png"), any(Map.class));
	}

	@Test
	void should_throw_exception_when_extracting_public_id_from_invalid_url() {
		when(cloudinary.uploader()).thenReturn(uploader);

		String url = "https://res.cloudinary.com/demo/image/no-upload/users/user-1.png";

		assertThrows(FileDeleteException.class, () -> fileService.deleteFile(url));
	}

	private byte[] loadExampleImageBytes() throws Exception {
		try (InputStream input = Objects.requireNonNull(
				getClass().getClassLoader().getResourceAsStream("img/example.png"),
				"Missing test image: img/example.png")) {
			return input.readAllBytes();
		}
	}
}
