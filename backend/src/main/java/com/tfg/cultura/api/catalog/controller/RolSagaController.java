package com.tfg.cultura.api.catalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.service.RolSagaService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog/rol-sagas")
@Tag(name = "Catalog - Rol Sagas", description = "Gestión de sagas de rol")
@RequiredArgsConstructor
public class RolSagaController {

    private final RolSagaService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RolSagaResponse> createRolSaga(
            @Valid @Parameter(description = "Datos de la saga de rol en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("rolSaga") RolSagaRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        RolSagaResponse response = service.create(request, image);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
