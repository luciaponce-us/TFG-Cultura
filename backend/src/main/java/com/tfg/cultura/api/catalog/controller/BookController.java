package com.tfg.cultura.api.catalog.controller;

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
                BookResponse response = bookService.create(bookRequest, image);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<BookResponse> getBook(@PathVariable String id) {
                BookResponse response = bookService.getById(id);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(response);
        }

        @GetMapping
        public ResponseEntity<Page<BookResponse>> getAllBooks(
                        @Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
                        @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size) {
                var responsePage = bookService.getAll(PageRequest.of(page, size));
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(responsePage);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteBook(@PathVariable String id) {
                bookService.delete(id);
                return ResponseEntity
                                .status(HttpStatus.NO_CONTENT).build();
        }

        @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<BookResponse> updateBook(
                        @PathVariable String id,
                        @Valid @Parameter(description = "Datos del libro en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("book") BookCreateRequest bookRequest,
                        @RequestPart(value = "image", required = false) MultipartFile image) {
                BookResponse response = bookService.update(id, bookRequest, image);
                return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(response);
        }
}
