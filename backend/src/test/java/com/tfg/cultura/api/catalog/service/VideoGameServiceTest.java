package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.VideoGame;
import com.tfg.cultura.api.catalog.model.dto.VideoGameRequest;
import com.tfg.cultura.api.catalog.repository.VideoGameRepository;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.factory.AppPropertiesFactory;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class VideoGameServiceTest {
    @Mock
    private VideoGameRepository videoGameRepository;
    @Mock
    private SectionService sectionService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private FileService fileService;
    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private VideoGameService service;

    private VideoGame videoGame;
    private VideoGameRequest request;

    @BeforeEach
    void setUp() {
        videoGame = CatalogFactory.validVideoGame();
        request = CatalogFactory.validVideoGameRequest();
    }

    @Test
    void should_not_throw_when_valid_video_game() {
        assertDoesNotThrow(() -> service.validate(videoGame));
    }

    // checkPurchasedAtAfterReleaseDate

    @Test
    void should_throw_when_purchased_at_before_release_date() {
        videoGame.setPurchasedAt(videoGame.getReleaseDate().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> service.validate(videoGame));
    }

    @Test
    void should_not_throw_when_purchased_is_equal_to_release_date() {
        videoGame.setPurchasedAt(videoGame.getReleaseDate());

        assertDoesNotThrow(() -> service.validate(videoGame));
    }

    @Test
    void should_not_throw_when_purchased_is_null() {
        videoGame.setPurchasedAt(null);

        assertDoesNotThrow(() -> service.validate(videoGame));
    }

    // checkUniqueNameAndPlatform

    @Test
    void should_throw_when_name_and_platform_already_exists() {
        when(videoGameRepository.existsByNameAndPlatform(videoGame.getName(), videoGame.getPlatform()))
                .thenReturn(true);

        assertThrows(ItemAlreadyExistsException.class, () -> service.validate(videoGame));
    }

    // fillSpecificFields

    @Test
    void should_fill_video_game_specific_fields() {
        assertDoesNotThrow(() -> service.fillSpecificFields(videoGame, request));
    }

    // loanDays

    @Test
    void should_return_zero_loan_days() {
        assertEquals(0, service.getLoanDays(request));
    }

    // getImageFolder

    @Test
    void should_return_image_folder() {
        assertEquals("cultura/items/videogames", service.getImageFolder());
    }

    // getDefaultImageUrl

    @Test
    void should_return_default_image_url() {
        AppProperties.DefaultImages defaultImages = AppPropertiesFactory.validAppProperties().defaultImages();
        when(appProperties.defaultImages()).thenReturn(defaultImages);

        assertEquals(defaultImages.videoGame(), service.getDefaultImageUrl());
    }

}