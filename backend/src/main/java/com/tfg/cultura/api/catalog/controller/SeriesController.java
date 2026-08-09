package com.tfg.cultura.api.catalog.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tfg.cultura.api.catalog.model.dto.SeriesRequest;
import com.tfg.cultura.api.catalog.model.dto.SeriesResponse;
import com.tfg.cultura.api.catalog.service.SeriesService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/catalog/series")
@Tag(name = "Catalog - Series", description = "Gestión de series")
public class SeriesController extends AbstractItemController<SeriesRequest, SeriesResponse, SeriesService> {
    public SeriesController(SeriesService seriesService) {
        super(seriesService);
    }

}
