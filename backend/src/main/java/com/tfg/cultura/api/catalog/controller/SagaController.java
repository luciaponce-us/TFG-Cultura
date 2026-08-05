package com.tfg.cultura.api.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.service.SagaService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog/sagas")
@RequiredArgsConstructor
@Tag(name = "Catalog - Sagas", description = "Gestión de sagas")
public class SagaController {

    private final SagaService sagaService;

    @PostMapping
    public ResponseEntity<Saga> createSaga(String name) {
        Saga saga = sagaService.createSaga(name);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saga);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Saga> getSagaByName(@PathVariable String name) {
        Saga saga = sagaService.findByName(name);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saga);
    }

    @GetMapping
    public ResponseEntity<List<Saga>> getAllSagas() {
        var sagas = sagaService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(sagas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Saga> updateSaga(@PathVariable String id, String name) {
        Saga updatedSaga = sagaService.updateSaga(id, name);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updatedSaga);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaga(@PathVariable String id) {
        sagaService.deleteSaga(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
