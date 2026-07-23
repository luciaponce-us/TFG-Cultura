package com.tfg.cultura.api.sections.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.service.SectionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
@Tag(name = "Sections - CRUD", description = "Gestión de secciones")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponse> createSection(@Valid @RequestBody SectionCreateRequest request) {
        SectionResponse response = sectionService.createSection(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAllSections(@RequestParam(required = false) String nameFilter) {
        List<SectionResponse> response = sectionService.getAllSections(nameFilter);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
