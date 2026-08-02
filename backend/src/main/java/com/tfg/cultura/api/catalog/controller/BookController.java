package com.tfg.cultura.api.catalog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.model.dto.BookCreateRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.service.BookService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog/books")
@RequiredArgsConstructor
@Tag(name = "Books - CRUD", description = "Gestión de libros")
public class BookController {

    private final BookService bookService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResponse> createBook(
            @Valid @Parameter(description = "Datos del libro en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("book") BookCreateRequest bookRequest,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        BookResponse response = bookService.createBook(bookRequest, image);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}
