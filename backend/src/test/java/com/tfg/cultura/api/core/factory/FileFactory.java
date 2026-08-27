package com.tfg.cultura.api.core.factory;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileFactory {
    public static MockMultipartFile mockImagePart() {
        return new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content".getBytes());
    }

    public static MockMultipartFile mockEmptyImagePart() {
        return new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]);
    }

    public static MultipartFile validPdf(int sizeInMB) {
        return new MockMultipartFile(
                "file",
                "document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                new byte[sizeInMB * 1024 * 1024]);
    }

    public static MultipartFile validImage(int sizeInMB) {
        return new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[sizeInMB * 1024 * 1024]);
    }

    public static MultipartFile fileWithoutContentType(int sizeInMB) {
        return new MockMultipartFile(
                "file",
                "document.txt",
                null,
                new byte[sizeInMB * 1024 * 1024]);
    }

}
