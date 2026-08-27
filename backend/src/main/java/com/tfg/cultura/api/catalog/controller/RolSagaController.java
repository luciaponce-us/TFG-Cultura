package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.service.RolSagaService;
import com.tfg.cultura.api.core.validation.annotations.ValidImage;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/catalog/rol-sagas")
@Tag(name = "Catalog - Rol Sagas", description = "Gestión de sagas de rol")
@RequiredArgsConstructor
public class RolSagaController {

	private final RolSagaService service;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RolSagaResponse> createRolSaga(
			@Valid @Parameter(description = "Datos de la saga de rol en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("rolSaga") RolSagaRequest request,
			@RequestPart(value = "image", required = false) @ValidImage MultipartFile image) {
		RolSagaResponse response = service.create(request, image);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RolSagaResponse> getRolSaga(@PathVariable String id) {
		RolSagaResponse response = service.getById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping
	public ResponseEntity<Page<RolSagaResponse>> getAllRolSagas(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<RolSagaResponse> response = service.getAll(PageRequest.of(page, size));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<RolSagaResponse> updateRolSaga(@PathVariable String id,
			@Valid @Parameter(description = "Datos de la saga de rol en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("rolSaga") RolSagaRequest request,
			@RequestPart(value = "image", required = false) @ValidImage MultipartFile image) {
		RolSagaResponse response = service.update(id, request, image);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRolSaga(@PathVariable String id) {
		service.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
