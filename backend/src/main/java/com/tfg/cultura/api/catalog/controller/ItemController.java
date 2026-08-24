package com.tfg.cultura.api.catalog.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.catalog.model.Item;
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

    @GetMapping
    public ResponseEntity<Page<Item>> getAllItems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String nameContains, @RequestParam(required = false) Set<String> categoryIds, @RequestParam(required = false) String sectionId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items = itemService.getAllItems(pageable, nameContains, categoryIds, sectionId);
        return ResponseEntity
        .status(HttpStatus.OK)
        .body(items);
    }
   
    
}
