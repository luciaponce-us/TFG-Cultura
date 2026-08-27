package com.tfg.cultura.api.catalog.controller;

import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.model.dto.ItemRequest;
import com.tfg.cultura.api.catalog.model.dto.ItemResponse;
import com.tfg.cultura.api.catalog.service.AbstractItemService;
import com.tfg.cultura.api.core.validation.annotations.ValidImage;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractItemController<TRequest extends ItemRequest, TResponse extends ItemResponse, TService extends AbstractItemService<?, ?, TRequest, TResponse>> {

    protected final TService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    protected ResponseEntity<TResponse> createItem(@Valid @Parameter(description = "Datos del item en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("item") TRequest request,
            @RequestPart(value = "image", required = false) @ValidImage MultipartFile image){
        TResponse response = service.create(request, image);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    };

    @GetMapping("/{id}")
    protected ResponseEntity<TResponse> getItem(@PathVariable String id) {
        TResponse response = service.getById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping
    protected ResponseEntity<Page<TResponse>> getAllItems(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String nameContains, @RequestParam(required = false) Set<String> categoryIds) {
        Page<TResponse> response = service.getAll(PageRequest.of(page, size), nameContains, categoryIds);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TResponse> updateItem(
            @PathVariable String id,
            @Valid @Parameter(description = "Datos del item en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("item") TRequest request,
            @RequestPart(value = "image", required = false) @ValidImage MultipartFile image){
        TResponse response = service.update(id, request, image);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
            }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
