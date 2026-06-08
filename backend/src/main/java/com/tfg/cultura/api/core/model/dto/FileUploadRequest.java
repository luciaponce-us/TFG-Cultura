package com.tfg.cultura.api.core.model.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder.Default;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadRequest {
    @NotBlank
    private MultipartFile file;
    private String folder;
    private String publicId;
    @Default
    private boolean overwrite = true;
    private String resourceType; // image, raw, auto
}
