package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.BoardGameRequest;
import com.tfg.cultura.api.catalog.model.dto.BoardGameResponse;
import com.tfg.cultura.api.catalog.service.BoardGameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/board-games")
@Tag(name = "Catalog - Board Games", description = "Gestión de juegos de mesa")
public class BoardGameController extends AbstractItemController<BoardGameRequest, BoardGameResponse, BoardGameService> {

	public BoardGameController(BoardGameService service) {
		super(service);
	}

}
