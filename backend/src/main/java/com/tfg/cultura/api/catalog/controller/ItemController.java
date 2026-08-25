package com.tfg.cultura.api.catalog.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.catalog.service.ItemService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog")
@Tag(name="Catalog", description = "Gestión de items del catálogo")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/types")
    public ResponseEntity<List<String>> getItemTypes() {
        List<String> itemTypes = itemService.getItemTypes();
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(itemTypes);
    }
   
}
