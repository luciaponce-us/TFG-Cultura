package com.tfg.cultura.api.catalog.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.model.VideoGame;
import com.tfg.cultura.api.catalog.model.dto.VideoGameRequest;
import com.tfg.cultura.api.catalog.model.dto.VideoGameResponse;
import com.tfg.cultura.api.catalog.repository.VideoGameRepository;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

@Service
public class VideoGameService
        extends AbstractItemService<VideoGame, VideoGameRepository, VideoGameRequest, VideoGameResponse> {

    private final AppProperties appProperties;

    public VideoGameService(VideoGameRepository videoGameRepository, SectionService sectionService,
            CategoryService categoryService, FileService fileService, AppProperties appProperties) {
        super(videoGameRepository, sectionService, categoryService, fileService, VideoGameResponse::new);
        this.appProperties = appProperties;
    }

    @Override
    protected String getImageFolder() {
        return "cultura/items/videogames";
    }

    @Override
    protected String getDefaultImageUrl() {
        return appProperties.defaultImages().videoGame();
    }

    @Override
    protected void validate(VideoGame item) {
        checkPurchasedAtAfterReleaseDate(item);
        checkUniqueNameAndPlatform(item);
    }

    private void checkPurchasedAtAfterReleaseDate(VideoGame item) {
        if (item.getPurchasedAt() != null) {
            LocalDate release = item.getReleaseDate();
            if (item.getPurchasedAt().isBefore(release)) {
                throw new IllegalArgumentException(
                        "La fecha de compra no puede ser anterior a la fecha de lanzamiento");
            }
        }
    }

    private void checkUniqueNameAndPlatform(VideoGame item) {
        boolean exists = repository.existsByNameAndPlatform(item.getName(), item.getPlatform());
        if (exists) {
            throw new ItemAlreadyExistsException(
                    Map.of("name", "Ya existe un videojuego con el mismo nombre y plataforma",
                            "platform", "Ya existe un videojuego con el mismo nombre y plataforma"));
        }
    }

    @Override
    protected VideoGame createEntity() {
        return VideoGame.builder().build();
    }

    @Override
    protected void fillSpecificFields(VideoGame item, VideoGameRequest request) {
        item.setPlatform(request.getPlatform());
        item.setReleaseDate(request.getReleaseDate());
        item.setTrailerUrl(sanitize(request.getTrailerUrl()));

        item.setLoanAvailable(false); // RN-21: Los videojuegos no se pueden prestar
    }

    @Override
    protected Integer getLoanDays(VideoGameRequest request) {
        return 0;
    }

}
