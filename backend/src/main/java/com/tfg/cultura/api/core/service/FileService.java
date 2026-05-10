package com.tfg.cultura.api.core.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tfg.cultura.api.core.exception.FileDeleteException;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.model.CustomMultipartFile;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class FileService {

    private Cloudinary cloudinary;

    public FileService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(FileUploadRequest request) {
        try {
            Map<String, Object> options = new HashMap<>();

            options.put("folder", request.getFolder());
            options.put("resource_type", request.getResourceType());

            if (request.getPublicId() != null) {
                options.put("public_id", request.getPublicId());
            }

            if (request.isOverwrite()) {
                options.put("overwrite", true);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    request.getFile().getBytes(),
                    options);

            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new FileUploadException(e.getMessage());
        }
    }

    public MultipartFile resizeImage(MultipartFile file, int width, int height) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Thumbnails.of(file.getInputStream())
                .size(width, height)
                .crop(net.coobird.thumbnailator.geometry.Positions.CENTER)
                .outputFormat("png")
                .toOutputStream(outputStream);

        String originalName = file.getOriginalFilename();

        String newName = (originalName != null)
                ? originalName.replaceAll("\\.[^.]+$", ".png")
                : "image.png";

        return new CustomMultipartFile(
                outputStream.toByteArray(),
                file.getName(),
                newName,
                "image/png");
    }

    public void deleteFile(String url) {
        try {
            String publicId = extractPublicId(url);

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());

        } catch (Exception e) {
            throw new FileDeleteException(e.getMessage());
        }
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
