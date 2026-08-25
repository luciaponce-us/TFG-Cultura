package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaNotFoundException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.dto.RolGameRequest;
import com.tfg.cultura.api.catalog.model.dto.RolGameResponse;
import com.tfg.cultura.api.catalog.repository.RolGameRepository;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class RolGameServiceTest {

    @Mock
    private RolGameRepository rolGameRepository;
    @Mock
    private SectionService sectionService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private FileService fileService;
    @Mock
    private AppProperties appProperties;
    @Mock
    private RolSagaService rolSagaService;

    @InjectMocks
    private RolGameService service;

    private RolGame rolGame;
    private RolGameRequest rolGameRequest;
    private RolSaga rolSaga;

    @BeforeEach
    void setUp() {
        rolSaga = CatalogFactory.validRolSaga();
        rolGame = CatalogFactory.validRolGame();
        rolGameRequest = CatalogFactory.validRolGameRequest();
    }

    // fillSpecificFields

    @Test
    void should_fill_specific_fields() {
        when(rolSagaService.findById(rolGame.getSaga().getId()))
                .thenReturn(rolSaga);

        service.fillSpecificFields(rolGame, rolGameRequest);

        assertEquals(rolSaga, rolGame.getSaga());
        assertEquals(rolGameRequest.getType(), rolGame.getType());
        assertEquals(rolSaga.getCategories(), rolGame.getCategories());
        assertEquals(rolSaga.getSection(), rolGame.getSection());
    }

    @Test
    void should_throw_exception_when_rol_saga_not_found() {
        when(rolSagaService.findById(rolGame.getSaga().getId()))
                .thenThrow(new RolSagaNotFoundException("RolSaga not found"));

        assertThrows(RolSagaNotFoundException.class, () -> service.fillSpecificFields(rolGame, rolGameRequest));
    }

    // findAllBySagaId

    @Test
    void should_find_all_by_saga_id() {
        when(rolSagaService.findById(rolSaga.getId()))
                .thenReturn(rolSaga);

        when(rolGameRepository.findAllBySaga(rolSaga))
                .thenReturn(List.of(rolGame));

        List<RolGameResponse> response = service.findAllBySagaId(rolSaga.getId());

        assertEquals(1, response.size());
        assertEquals(rolGame.getId(), response.get(0).getId());
    }

    @Test
    void should_throw_exception_when_rol_saga_not_found_in_find_all_by_saga_id() {
        when(rolSagaService.findById(rolSaga.getId()))
                .thenThrow(new RolSagaNotFoundException("RolSaga not found"));

        assertThrows(RolSagaNotFoundException.class, () -> service.findAllBySagaId(rolSaga.getId()));
    }

}
