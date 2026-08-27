package com.tfg.cultura.api.core.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

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
