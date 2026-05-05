package com.tfg.cultura.api.suggestions.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.suggestions.model.dto.SuggestionCreateRequest;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionResponse;
import com.tfg.cultura.api.suggestions.service.SuggestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/suggestions")
@Tag(name = "Suggestions", description = "Módulo de gestión de sugerencias")
public class SuggestionController {

    private final SuggestionService service;

    public SuggestionController(SuggestionService service) {
        this.service = service;
    }

    @Operation(summary = "Leer todas las sugerencias", description = "Como usuario, quiero poder leer todas las sugerencias realizadas por otros usuarios para conocer las necesidades y propuestas de la comunidad.")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Sugerencias obtenidas correctamente") })
    @GetMapping
    public ResponseEntity<List<SuggestionResponse>> getAll() {
        List<SuggestionResponse> response = service.getAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "RF-08: Realizar sugerencias", description = "Como usuario registrado, quiero poder escribir sugerencias para que la Delegación de Cultura tenga en cuenta mis necesidades a la hora de mejorar sus servicios.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sugerencia creada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Autor no encontrado")
    })
    public ResponseEntity<SuggestionResponse> create(@Valid @RequestBody SuggestionCreateRequest request) {
        SuggestionResponse response = service.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}/support")
    public ResponseEntity<SuggestionResponse> supportSuggestion(@PathVariable String id) {
        SuggestionResponse response = service.supportSuggestion(id);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

    @PutMapping("/{id}/support/stop")
    public ResponseEntity<SuggestionResponse> stopSupportingSuggestion(@PathVariable String id) {
        SuggestionResponse response = service.stopSupportingSuggestion(id);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(response);
    }

}
