package com.tfg.cultura.api.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.catalog.model.dto.RolGameRequest;
import com.tfg.cultura.api.catalog.model.dto.RolGameResponse;
import com.tfg.cultura.api.catalog.service.RolGameService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/catalog/rol-games")
@Tag(name = "Catalog - Rol Games", description = "Gestión de juegos de rol")
public class RolGameController extends AbstractItemController<RolGameRequest, RolGameResponse, RolGameService> {

    public RolGameController(RolGameService rolGameService) {
        super(rolGameService);
    }

    @GetMapping("/saga/{sagaId}")
    public ResponseEntity<List<RolGameResponse>> findBySagaId(@PathVariable String sagaId) {
        List<RolGameResponse> response = service.findBySagaId(sagaId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
