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
	@NotBlank
	private String folder;
	@NotBlank
	private String className;
	@NotBlank
	private String id;
	private Integer width;
	private Integer height;
	private String field;
	private String defaultFileUrl;
	@Default
	private boolean overwrite = true;
	private String resourceType; // image, raw, auto
}
