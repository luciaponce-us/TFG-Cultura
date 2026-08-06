package com.tfg.cultura.api.catalog.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tfg.cultura.api.catalog.exception.CatalogExceptionHandler;
import com.tfg.cultura.api.catalog.exception.saga.SagaAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.saga.SagaNotFoundException;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.service.SagaService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class SagaControllerTest extends BaseControllerTest {

    @Mock
    private SagaService sagaService;

    private static final String BASE_URL = "/api/catalog/sagas";
    private static final String SAGA_URL = BASE_URL + "/{id}";
    private static final String GET_SAGA_URL = BASE_URL + "/{name}";

    private Saga saga;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SagaController controller = new SagaController(sagaService);
        mockMvc = buildMockMvc(controller, CatalogExceptionHandler.class);
        saga = Saga.builder()
                .id("1")
                .name("Test Saga")
                .build();
    }

    // ====================== CREATE ======================

    @Test
    void should_create_saga_successfully() throws Exception {
        when(sagaService.createSaga(anyString())).thenReturn(saga);

        mockMvc.perform(post(BASE_URL)
                .param("name", saga.getName()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(saga.getId()))
                .andExpect(jsonPath("$.name").value(saga.getName()));

        verify(sagaService).createSaga(saga.getName());
    }

    @Test
    void should_return_conflict_when_saga_already_exists() throws Exception {
        when(sagaService.createSaga(anyString()))
                .thenThrow(new SagaAlreadyExistsException(saga.getName()));

        mockMvc.perform(post(BASE_URL)
                .param("name", saga.getName()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Saga Already Exists"));

        verify(sagaService).createSaga(saga.getName());
    }

    // ====================== GET BY NAME ======================

    @Test
    void should_get_saga_by_name() throws Exception {
        when(sagaService.findByName(anyString())).thenReturn(saga);

        mockMvc.perform(get(GET_SAGA_URL, saga.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saga.getId()))
                .andExpect(jsonPath("$.name").value(saga.getName()));

        verify(sagaService).findByName(saga.getName());
    }

    @Test
    void should_return_404_when_saga_by_name_not_found() throws Exception {
        when(sagaService.findByName(anyString()))
                .thenThrow(new SagaNotFoundException(saga.getName()));

        mockMvc.perform(get(GET_SAGA_URL, "missing-saga"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Saga Not Found"));

        verify(sagaService).findByName("missing-saga");
    }

    // ====================== GET ALL ======================

    @Test
    void should_get_all_sagas() throws Exception {
        when(sagaService.findAll()).thenReturn(List.of(saga));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(saga.getId()))
                .andExpect(jsonPath("$[0].name").value(saga.getName()));

        verify(sagaService).findAll();
    }

    // ====================== UPDATE ======================

    @Test
    void should_update_saga_successfully() throws Exception {
        Saga updatedSaga = Saga.builder()
                .id(saga.getId())
                .name("Updated Saga")
                .build();

        when(sagaService.updateSaga(anyString(), anyString())).thenReturn(updatedSaga);

        mockMvc.perform(put(SAGA_URL, saga.getId())
                .param("name", updatedSaga.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saga.getId()))
                .andExpect(jsonPath("$.name").value(updatedSaga.getName()));

        verify(sagaService).updateSaga(saga.getId(), updatedSaga.getName());
    }

    @Test
    void should_return_404_when_updating_missing_saga() throws Exception {
        when(sagaService.updateSaga(anyString(), anyString()))
                .thenThrow(new SagaNotFoundException("missing-id"));

        mockMvc.perform(put(SAGA_URL, "missing-id")
                .param("name", "Updated Saga"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Saga Not Found"));

        verify(sagaService).updateSaga("missing-id", "Updated Saga");
    }

    @Test
    void should_return_conflict_when_updating_to_existing_saga_name() throws Exception {
        when(sagaService.updateSaga(anyString(), anyString()))
                .thenThrow(new SagaAlreadyExistsException("Existing Saga"));

        mockMvc.perform(put(SAGA_URL, saga.getId())
                .param("name", "Existing Saga"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Saga Already Exists"));

        verify(sagaService).updateSaga(saga.getId(), "Existing Saga");
    }

    // ====================== DELETE ======================

    @Test
    void should_delete_saga_successfully() throws Exception {
        mockMvc.perform(delete(SAGA_URL, saga.getId()))
                .andExpect(status().isNoContent());

        verify(sagaService).deleteSaga(saga.getId());
    }

    @Test
    void should_return_404_when_deleting_missing_saga() throws Exception {
        doThrow(new SagaNotFoundException("missing-id"))
                .when(sagaService).deleteSaga(anyString());

        mockMvc.perform(delete(SAGA_URL, "missing-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Saga Not Found"));

        verify(sagaService).deleteSaga("missing-id");
    }
}
