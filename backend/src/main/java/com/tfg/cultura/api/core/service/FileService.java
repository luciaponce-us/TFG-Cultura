package com.tfg.cultura.api.core.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tfg.cultura.api.core.exception.file.FileDeleteException;
import com.tfg.cultura.api.core.exception.file.FileUploadException;
import com.tfg.cultura.api.core.exception.file.InvalidFileSizeException;
import com.tfg.cultura.api.core.exception.file.InvalidFileTypeException;
import com.tfg.cultura.api.core.model.CustomMultipartFile;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.utils.LoggerSanitizer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

	private Cloudinary cloudinary;
	private static final Integer MAX_FILE_SIZE_MB = 2;

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring dependency injection")
	public FileService(Cloudinary cloudinary) {
		this.cloudinary = cloudinary;
	}

	public String uploadFile(FileUploadRequest request) {
		try {
			Map<String, Object> options = new HashMap<>();

			options.put("folder", request.getFolder());
			options.put("resource_type", request.getResourceType());
			options.put("type", "upload");
			options.put("overwrite", request.isOverwrite());

			if (request.getPublicId() != null) {
				options.put("public_id", request.getPublicId());
			}

			@SuppressWarnings("unchecked")
			Map<String, Object> uploadResult = cloudinary.uploader().upload(request.getFile().getBytes(), options);

			return uploadResult.get("secure_url").toString();

		} catch (Exception e) {
			throw new FileUploadException(e.getMessage());
		}
	}

	public void deleteFile(String url) throws FileDeleteException {
		try {
			String publicId = extractPublicId(url);
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		} catch (Exception e) {
			throw new FileDeleteException(e.getMessage());
		}
	}

	public String uploadPdf(String className, String id, MultipartFile pdf, String folder, String defaultPdfUrl,
			Logger logger, String field) throws FileUploadException {
		if (pdf != null && !pdf.isEmpty()) {
			String sanitizedId = LoggerSanitizer.sanitize(id);
			String publicId = className + "_" + sanitizedId;

			validateFileSize(pdf, logger, field);

			FileUploadRequest request = FileUploadRequest.builder().file(pdf).folder(folder).publicId(publicId)
					.resourceType("raw").build();

			return uploadFile(request);
		}
		return defaultPdfUrl;
	}

	public String uploadImage(String className, String id, MultipartFile image, String folder, String defaultImageUrl,
			Integer width, Integer height, Logger logger, String field) throws FileUploadException {
		if (image != null && !image.isEmpty()) {
			String sanitizedId = LoggerSanitizer.sanitize(id);
			String publicId = className + "_" + sanitizedId;

			validateImageSize(image, logger, field);
			MultipartFile resizedImage = resizeImage(image, width, height);

			FileUploadRequest request = FileUploadRequest.builder().file(resizedImage).folder(folder).publicId(publicId)
					.resourceType("image").build();

			return uploadFile(request);
		}
		return defaultImageUrl;
	}

	public String uploadImage(String className, String id, MultipartFile image, String folder, String defaultImageUrl,
			int width, int height) throws FileUploadException {
		return uploadImage(className, id, image, folder, defaultImageUrl, width, height,
				LoggerFactory.getLogger("appLogger"), "image");
	}

	public String updateImage(String oldUrl, String className, String id, MultipartFile newImage, String folder,
			String defaultImageUrl, Integer width, Integer height, Logger logger, String field)
			throws FileDeleteException, FileUploadException {
		if (newImage != null && !newImage.isEmpty()) {
			if (oldUrl != null && !oldUrl.equals(defaultImageUrl)) {
				deleteFile(oldUrl);
			}
			return uploadImage(className, id, newImage, folder, defaultImageUrl, width, height, logger, field);
		}
		return oldUrl;
	}

	public String updateImage(String oldUrl, String className, String id, MultipartFile newImage, String folder,
			String defaultImageUrl, int width, int height) throws FileDeleteException, FileUploadException {
		return updateImage(oldUrl, className, id, newImage, folder, defaultImageUrl, width, height,
				LoggerFactory.getLogger("appLogger"), "image");
	}

	public MultipartFile resizeImage(MultipartFile file, int width, int height) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			Thumbnails.of(file.getInputStream()).size(width, height)
					.crop(net.coobird.thumbnailator.geometry.Positions.CENTER).outputFormat("png")
					.toOutputStream(outputStream);

			String originalName = file.getOriginalFilename();

			String newName = (originalName != null) ? originalName.replaceAll("\\.[^.]+$", ".png") : "image.png";

			return new CustomMultipartFile(outputStream.toByteArray(), file.getName(), newName, "image/png");
		} catch (IOException e) {
			throw new FileUploadException("Error al redimensionar la imagen: " + e.getMessage());
		}
	}

	private void validateFileSize(MultipartFile file, Logger logger, String field) throws InvalidFileSizeException {
		long maxSizeBytes = MAX_FILE_SIZE_MB * 1024 * 1024;
		if (file.getSize() > maxSizeBytes) {
			throw new InvalidFileSizeException(logger, field, MAX_FILE_SIZE_MB);
		}
	}

	private void validateImage(MultipartFile file, Logger logger, String field) throws InvalidFileTypeException {
		String contentType = file.getContentType();
		List<String> allowedTypes = List.of("image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

		if (contentType == null || !contentType.startsWith("image/") || !allowedTypes.contains(contentType)) {
			String allowedTypesMessage = "JPEG, JPG, PNG, WebP o GIF";
			throw new InvalidFileTypeException(logger, field, allowedTypesMessage);
		}
	}

	public void validateImageSize(MultipartFile file, Logger logger, String field)
			throws InvalidFileSizeException, InvalidFileTypeException {
		validateImage(file, logger, field);
		validateFileSize(file, logger, field);
	}

	private String extractPublicId(String url) {
		try {
			String[] parts = url.split("/upload/");
			String afterUpload = parts[1];

			// quitar versión si existe (v1234567890/)
			afterUpload = afterUpload.replaceAll("v\\d+/", "");

			// quitar extensión
			int dotIndex = afterUpload.lastIndexOf(".");
			if (dotIndex > 0) {
				afterUpload = afterUpload.substring(0, dotIndex);
			}

			return afterUpload;
		} catch (Exception e) {
			throw new FileDeleteException(e.getMessage());
		}
	}

}
