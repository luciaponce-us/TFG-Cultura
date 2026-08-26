package com.tfg.cultura.api.catalog.controller;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.dto.RolGameResponse;
import com.tfg.cultura.api.catalog.service.RolGameService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class RolGameControllerTest extends BaseControllerTest {

    @Mock
    private RolGameService rolGameService;

    private RolGameResponse rolGameResponse;

    private static final String BASE_URL = "/api/catalog/rol-games";
    private static final String ROL_GAMES_BY_SAGA_URL = BASE_URL + "/saga/{sagaId}";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        RolGameController controller = new RolGameController(rolGameService);
        mockMvc = buildMockMvc(controller);
        rolGameResponse = new RolGameResponse(CatalogFactory.validRolGame());
    }

    // findAllBySagaId

    @Test
    void should_find_all_by_saga_id() throws Exception {
        when(rolGameService.findAllBySagaId("saga-id"))
                .thenReturn(List.of(rolGameResponse));

        mockMvc.perform(get(ROL_GAMES_BY_SAGA_URL, "saga-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rolGameResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(rolGameResponse.getName()))
                .andExpect(jsonPath("$[0].description").value(rolGameResponse.getDescription()))
                .andExpect(jsonPath("$[0].type").value(rolGameResponse.getType().toString()))
                .andExpect(jsonPath("$[0].saga.id").value(rolGameResponse.getSaga().getId()))
                .andExpect(jsonPath("$[0].section.id").value(rolGameResponse.getSection().getId()))
                .andExpect(jsonPath("$[0].imageUrl").value(rolGameResponse.getImageUrl()));
    }

    @Test
    void should_return_empty_list_when_no_rol_games_found_for_saga() throws Exception {
        when(rolGameService.findAllBySagaId("saga-id"))
                .thenReturn(List.of());

        mockMvc.perform(get(ROL_GAMES_BY_SAGA_URL, "saga-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void should_return_404_when_saga_not_found() throws Exception {
        when(rolGameService.findAllBySagaId("non-existent-saga-id"))
                .thenThrow(new com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaNotFoundException("non-existent-saga-id"));

        mockMvc.perform(get(ROL_GAMES_BY_SAGA_URL, "non-existent-saga-id"))
                .andExpect(status().isNotFound());
    }
    
}
