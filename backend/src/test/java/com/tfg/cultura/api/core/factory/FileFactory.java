package com.tfg.cultura.api.core.factory;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

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
}
