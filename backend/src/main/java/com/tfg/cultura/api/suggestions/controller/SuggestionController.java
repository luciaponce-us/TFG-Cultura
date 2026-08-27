package com.tfg.cultura.api.suggestions.controller;

import com.tfg.cultura.api.suggestions.model.dto.SuggestionCreateRequest;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionResponse;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.suggestions.service.SuggestionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/suggestions")
@Tag(name = "Suggestions", description = "Módulo de gestión de sugerencias")
public class SuggestionController implements SuggestionControllerInterface {

	private final SuggestionService service;

	@Override
	@GetMapping
	public ResponseEntity<Page<SuggestionResponse>> getAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) SuggestionType type,
			@RequestParam(required = false) String text,
			@RequestParam(required = false, defaultValue = "false") Boolean orderByCreationDate,
			@RequestParam(required = false) Boolean supportedByAdmins,
			@RequestParam(required = false, defaultValue = "false") Boolean mySuggestions

	) {
		Page<SuggestionResponse> response = service.getAllWithFilters(type, text, orderByCreationDate,
				supportedByAdmins, mySuggestions, page, size);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<SuggestionResponse> getById(@PathVariable String id) {
		SuggestionResponse response = service.getById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@PostMapping(value = "/create")
	public ResponseEntity<SuggestionResponse> create(@Valid @RequestBody SuggestionCreateRequest request) {
		SuggestionResponse response = service.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Override
	@PutMapping("/{id}/toggle-support")
	public ResponseEntity<SuggestionResponse> toggleSupport(@PathVariable String id) {
		SuggestionResponse response = service.toggleSupport(id);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Override
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		service.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
