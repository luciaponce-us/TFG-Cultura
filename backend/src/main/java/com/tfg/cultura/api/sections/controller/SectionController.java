package com.tfg.cultura.api.sections.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.service.SectionService;
import com.tfg.cultura.api.sections.service.SectionUpdateService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
@Tag(name = "Sections - CRUD", description = "Gestión de secciones")
public class SectionController {

    private final SectionService sectionService;
    private final SectionUpdateService sectionUpdateService;

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

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponse> getSectionById(@PathVariable String id) {
        SectionResponse response = sectionService.getSectionById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionResponse> updateSection(@PathVariable String id, @Valid @RequestBody SectionCreateRequest request) {
        SectionResponse response = sectionUpdateService.updateSection(id, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{sectionId}/managers/{managerUsername}/remove")
    public ResponseEntity<SectionResponse> removeManagerFromSection(@PathVariable String sectionId, @PathVariable String managerUsername) {
        SectionResponse response = sectionUpdateService.removeManagerFromSection(sectionId, managerUsername);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{sectionId}/collaborators/{collaboratorUsername}/remove")
    public ResponseEntity<SectionResponse> removeCollaboratorFromSection(@PathVariable String sectionId, @PathVariable String collaboratorUsername) {
        SectionResponse response = sectionUpdateService.removeCollaboratorFromSection(sectionId, collaboratorUsername);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{sectionId}/managers/{managerUsername}/add")
    public ResponseEntity<SectionResponse> addManagerToSection(@PathVariable String sectionId, @PathVariable String managerUsername) {
        SectionResponse response = sectionUpdateService.addManagerToSection(sectionId, managerUsername);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PutMapping("/{sectionId}/collaborators/{collaboratorUsername}/add")
    public ResponseEntity<SectionResponse> addCollaboratorToSection(@PathVariable String sectionId, @PathVariable String collaboratorUsername) {
        SectionResponse response = sectionUpdateService.addCollaboratorToSection(sectionId, collaboratorUsername);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    

}
