package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.VideoGameRequest;
import com.tfg.cultura.api.catalog.model.dto.VideoGameResponse;
import com.tfg.cultura.api.catalog.service.VideoGameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/videogames")
@Tag(name = "Catalog - Video Games", description = "Gestión de videojuegos")
public class VideoGameController extends AbstractItemController<VideoGameRequest, VideoGameResponse, VideoGameService> {
	public VideoGameController(VideoGameService videoGameService) {
		super(videoGameService);
	}

}
