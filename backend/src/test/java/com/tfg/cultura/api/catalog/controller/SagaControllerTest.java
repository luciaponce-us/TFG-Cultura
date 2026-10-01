package com.tfg.cultura.api.catalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.SagaRequest;
import com.tfg.cultura.api.catalog.service.SagaService;
import com.tfg.cultura.api.core.factory.ExceptionsFactory;
import com.tfg.cultura.api.utils.BaseControllerTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

class SagaControllerTest extends BaseControllerTest {

	@Mock
	private SagaService sagaService;

	private static final String BASE_URL = "/api/catalog/sagas";
	private static final String SAGA_URL = BASE_URL + "/{id}";
	private static final String GET_SAGA_URL = BASE_URL + "/{name}";

	private Saga saga;
	private SagaRequest sagaRequest;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		SagaController controller = new SagaController(sagaService);
		mockMvc = buildMockMvc(controller);
		sagaRequest = CatalogFactory.validSagaRequest();
		saga = CatalogFactory.validSaga();
	}

	// ====================== CREATE ======================

	@Test
	void should_create_saga_successfully() throws Exception {
		when(sagaService.createSaga(any(SagaRequest.class))).thenReturn(saga);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(sagaRequest)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(saga.getId()))
				.andExpect(jsonPath("$.name").value(saga.getName()));

		verify(sagaService).createSaga(any(SagaRequest.class));
	}

	@Test
	void should_return_conflict_when_saga_already_exists() throws Exception {
		when(sagaService.createSaga(any(SagaRequest.class))).thenThrow(ExceptionsFactory.duplicationException("name"));

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(sagaRequest)))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.message").exists());

		verify(sagaService).createSaga(any(SagaRequest.class));
	}

	@Test
	void should_return_bad_request_when_creating_saga_with_blank_name() throws Exception {
		SagaRequest invalidRequest = SagaRequest.builder().name(" ").build();

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(invalidRequest)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(sagaService);
	}

	@Test
	void should_return_bad_request_when_creating_saga_with_short_name() throws Exception {
		SagaRequest invalidRequest = SagaRequest.builder().name("Sa").build();

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(invalidRequest)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(sagaService);
	}

	// ====================== GET BY NAME ======================

	@Test
	void should_get_saga_by_name() throws Exception {
		when(sagaService.findByName(anyString())).thenReturn(saga);

		mockMvc.perform(get(GET_SAGA_URL, saga.getName())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(saga.getId())).andExpect(jsonPath("$.name").value(saga.getName()));

		verify(sagaService).findByName(saga.getName());
	}

	@Test
	void should_return_404_when_saga_by_name_not_found() throws Exception {
		when(sagaService.findByName(anyString())).thenThrow(ExceptionsFactory.notFoundException);

		mockMvc.perform(get(GET_SAGA_URL, "missing-saga")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());

		verify(sagaService).findByName("missing-saga");
	}

	// ====================== GET ALL ======================

	@Test
	void should_get_all_sagas() throws Exception {
		when(sagaService.findAll()).thenReturn(List.of(saga));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(saga.getId()))
				.andExpect(jsonPath("$[0].name").value(saga.getName()));

		verify(sagaService).findAll();
	}

	// ====================== UPDATE ======================

	@Test
	void should_update_saga_successfully() throws Exception {
		Saga updatedSaga = Saga.builder().id(saga.getId()).name("Updated Saga").build();
		SagaRequest updateRequest = SagaRequest.builder().name(updatedSaga.getName()).build();

		when(sagaService.updateSaga(anyString(), any(SagaRequest.class))).thenReturn(updatedSaga);

		mockMvc.perform(
				put(SAGA_URL, saga.getId()).contentType(MediaType.APPLICATION_JSON).content(toJson(updateRequest)))
				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(saga.getId()))
				.andExpect(jsonPath("$.name").value(updatedSaga.getName()));

		verify(sagaService).updateSaga(eq(saga.getId()), any(SagaRequest.class));
	}

	@Test
	void should_return_404_when_updating_missing_saga() throws Exception {
		SagaRequest updateRequest = SagaRequest.builder().name("Updated Saga").build();
		when(sagaService.updateSaga(anyString(), any(SagaRequest.class)))
				.thenThrow(ExceptionsFactory.notFoundException);

		mockMvc.perform(
				put(SAGA_URL, "missing-id").contentType(MediaType.APPLICATION_JSON).content(toJson(updateRequest)))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.message").exists());

		verify(sagaService).updateSaga(eq("missing-id"), any(SagaRequest.class));
	}

	@Test
	void should_return_conflict_when_updating_to_existing_saga_name() throws Exception {
		SagaRequest updateRequest = SagaRequest.builder().name("Existing Saga").build();
		when(sagaService.updateSaga(anyString(), any(SagaRequest.class)))
				.thenThrow(ExceptionsFactory.duplicationException("name"));

		mockMvc.perform(
				put(SAGA_URL, saga.getId()).contentType(MediaType.APPLICATION_JSON).content(toJson(updateRequest)))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.message").exists());

		verify(sagaService).updateSaga(eq(saga.getId()), any(SagaRequest.class));
	}

	@Test
	void should_return_bad_request_when_updating_saga_with_blank_name() throws Exception {
		SagaRequest invalidRequest = SagaRequest.builder().name("").build();

		mockMvc.perform(
				put(SAGA_URL, saga.getId()).contentType(MediaType.APPLICATION_JSON).content(toJson(invalidRequest)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(sagaService);
	}

	// ====================== DELETE ======================

	@Test
	void should_delete_saga_successfully() throws Exception {
		mockMvc.perform(delete(SAGA_URL, saga.getId())).andExpect(status().isNoContent());

		verify(sagaService).deleteSaga(saga.getId());
	}

	@Test
	void should_return_404_when_deleting_missing_saga() throws Exception {
		doThrow(ExceptionsFactory.notFoundException).when(sagaService).deleteSaga(anyString());

		mockMvc.perform(delete(SAGA_URL, "missing-id")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());

		verify(sagaService).deleteSaga("missing-id");
	}
}
