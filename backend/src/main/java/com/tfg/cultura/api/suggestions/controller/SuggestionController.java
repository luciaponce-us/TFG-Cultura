package com.tfg.cultura.api.suggestions.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.suggestions.model.dto.SuggestionCreateRequest;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionResponse;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.suggestions.service.SuggestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/suggestions")
@Tag(name = "Suggestions", description = "Módulo de gestión de sugerencias")
public class SuggestionController {

    private final SuggestionService service;

    @Operation(summary = "Leer todas las sugerencias", description = "Como usuario, quiero poder leer todas las sugerencias realizadas por otros usuarios para conocer las necesidades y propuestas de la comunidad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencias obtenidas correctamente") })
    @GetMapping
    public ResponseEntity<Page<SuggestionResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SuggestionType type,
            @RequestParam(required = false) String text,
            @RequestParam(required = false, defaultValue = "false") Boolean orderByCreationDate,
            @RequestParam(required = false) Boolean supportedByAdmins,
            @RequestParam(required = false, defaultValue = "false") Boolean mySuggestions

    ) {
        Page<SuggestionResponse> response = service.getAllWithFilters(type, text, orderByCreationDate,
                supportedByAdmins, mySuggestions, page,
                size);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Leer sugerencia por ID", description = "Como usuario, quiero poder leer una sugerencia específica para conocer su contenido y los detalles de la misma.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencia obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Sugerencia no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SuggestionResponse> getById(@PathVariable String id) {
        SuggestionResponse response = service.getById(id);
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

    @Operation(summary = "RF-09: Apoyar sugerencias", description = "Como usuario registrado, quiero poder expresar que estoy de acuerdo con una sugerencia para que la sugerencia que apoyo sea tenida en cuenta con mayor prioridad.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencia modificada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Sugerencia o autor no encontrado"),
            @ApiResponse(responseCode = "400", description = "No puedes apoyar esta sugerencia porque eres su autor (RN-06) o ya la apoyas")
    })
    @PutMapping("/{id}/support")
    public ResponseEntity<SuggestionResponse> supportSuggestion(@PathVariable String id) {
        SuggestionResponse response = service.supportSuggestion(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Dejar de apoyar sugerencias", description = "Como usuario registrado, quiero poder expresar que ya no estoy de acuerdo con una sugerencia para cambiar de opinión.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sugerencia modificada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Sugerencia o autor no encontrado"),
            @ApiResponse(responseCode = "400", description = "Ya no apoyabas esta sugerencia")
    })
    @PutMapping("/{id}/support/stop")
    public ResponseEntity<SuggestionResponse> stopSupportingSuggestion(@PathVariable String id) {
        SuggestionResponse response = service.stopSupportingSuggestion(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Eliminar sugerencia", description = "Como usuario registrado, quiero poder eliminar una sugerencia que he realizado para retirar una propuesta que ya no considero relevante o adecuada.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sugerencia eliminada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "404", description = "Sugerencia o autor no encontrado"),
            @ApiResponse(responseCode = "401", description = "Usuario no autenticado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
