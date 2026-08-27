package com.tfg.cultura.api.catalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaNotFoundException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.service.RolSagaService;
import com.tfg.cultura.api.core.factory.FileFactory;
import com.tfg.cultura.api.utils.BaseControllerTest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

class RolSagaControllerTest extends BaseControllerTest {

	@Mock
	private RolSagaService rolSagaService;

	private RolSagaResponse rolSagaResponse;
	private RolSagaRequest rolSagaRequest;

	private static final String BASE_URL = "/api/catalog/rol-sagas";

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		RolSagaController controller = new RolSagaController(rolSagaService);
		mockMvc = buildMockMvc(controller);
		initTestData();
	}

	private void initTestData() {
		rolSagaResponse = new RolSagaResponse(CatalogFactory.validRolSaga());
		rolSagaRequest = CatalogFactory.validRolSagaRequest();
	}

	private MockMultipartFile mockRolSagaPart(RolSagaRequest request) throws Exception {
		return new MockMultipartFile("rolSaga", "rolsaga.json", MediaType.APPLICATION_JSON_VALUE,
				toJson(request).getBytes(StandardCharsets.UTF_8));
	}

	// createRolSaga

	@Test
	void should_create_rol_saga() throws Exception {
		when(rolSagaService.create(any(RolSagaRequest.class), any())).thenReturn(rolSagaResponse);

		MockMultipartFile rolSagaPart = mockRolSagaPart(rolSagaRequest);
		MockMultipartFile imagePart = FileFactory.mockImagePart();

		mockMvc.perform(
				multipart(BASE_URL).file(rolSagaPart).file(imagePart).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(rolSagaResponse.getId()))
				.andExpect(jsonPath("$.name").value(rolSagaResponse.getName()))
				.andExpect(jsonPath("$.description").value(rolSagaResponse.getDescription()))
				.andExpect(jsonPath("$.section.id").value(rolSagaResponse.getSection().getId()))
				.andExpect(jsonPath("$.imageUrl").value(rolSagaResponse.getImageUrl()));

	}

	@Test
	void should_create_rol_saga_without_image() throws Exception {
		when(rolSagaService.create(any(RolSagaRequest.class), isNull())).thenReturn(rolSagaResponse);

		MockMultipartFile rolSagaPart = mockRolSagaPart(rolSagaRequest);

		mockMvc.perform(multipart(BASE_URL).file(rolSagaPart).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(rolSagaResponse.getId()))
				.andExpect(jsonPath("$.name").value(rolSagaResponse.getName()))
				.andExpect(jsonPath("$.description").value(rolSagaResponse.getDescription()))
				.andExpect(jsonPath("$.section.id").value(rolSagaResponse.getSection().getId()))
				.andExpect(jsonPath("$.imageUrl").value(rolSagaResponse.getImageUrl()));
	}

	@Test
	void should_return_bad_request_when_invalid_rol_saga_request() throws Exception {
		MockMultipartFile rolSagaPart = mockRolSagaPart(
				RolSagaRequest.builder().name("").description("").sectionId("").build());

		mockMvc.perform(multipart(BASE_URL).file(rolSagaPart).contentType(MediaType.MULTIPART_FORM_DATA))
				.andExpect(status().isBadRequest());
	}

	// getRolSaga

	@Test
	void should_return_rol_saga_by_id() throws Exception {
		when(rolSagaService.getById(anyString())).thenReturn(rolSagaResponse);

		mockMvc.perform(get(BASE_URL + "/{id}", rolSagaResponse.getId())).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(rolSagaResponse.getId()))
				.andExpect(jsonPath("$.name").value(rolSagaResponse.getName()))
				.andExpect(jsonPath("$.description").value(rolSagaResponse.getDescription()))
				.andExpect(jsonPath("$.section.id").value(rolSagaResponse.getSection().getId()))
				.andExpect(jsonPath("$.imageUrl").value(rolSagaResponse.getImageUrl()));
	}

	@Test
	void should_return_not_found_when_rol_saga_not_found() throws Exception {
		when(rolSagaService.getById(anyString())).thenThrow(new RolSagaNotFoundException("non-existent-id"));

		mockMvc.perform(get(BASE_URL + "/{id}", "non-existent-id")).andExpect(status().isNotFound());
	}

	// getAllRolSagas

	@Test
	void should_return_all_rol_sagas() throws Exception {

		Page<RolSagaResponse> page = new PageImpl<>(List.of(rolSagaResponse), PageRequest.of(0, 10), 1);

		when(rolSagaService.getAll(PageRequest.of(0, 10))).thenReturn(page);

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content[*].id").value(rolSagaResponse.getId()));
	}

	// updateRolSaga

	@Test
	void should_update_rol_saga() throws Exception {
		when(rolSagaService.update(anyString(), any(RolSagaRequest.class), any())).thenReturn(rolSagaResponse);

		MockMultipartFile rolSagaPart = mockRolSagaPart(rolSagaRequest);
		MockMultipartFile imagePart = FileFactory.mockImagePart();

		mockMvc.perform(multipart(BASE_URL + "/{id}", rolSagaResponse.getId()).file(rolSagaPart).file(imagePart)
				.with(request -> {
					request.setMethod("PUT");
					return request;
				}).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(rolSagaResponse.getId()))
				.andExpect(jsonPath("$.name").value(rolSagaResponse.getName()))
				.andExpect(jsonPath("$.description").value(rolSagaResponse.getDescription()))
				.andExpect(jsonPath("$.section.id").value(rolSagaResponse.getSection().getId()))
				.andExpect(jsonPath("$.imageUrl").value(rolSagaResponse.getImageUrl()));
	}

	@Test
	void should_return_not_found_when_updating_non_existent_rol_saga() throws Exception {
		when(rolSagaService.update(anyString(), any(RolSagaRequest.class), any()))
				.thenThrow(new RolSagaNotFoundException("non-existent-id"));

		MockMultipartFile rolSagaPart = mockRolSagaPart(rolSagaRequest);

		mockMvc.perform(multipart(BASE_URL + "/{id}", "non-existent-id").file(rolSagaPart).with(request -> {
			request.setMethod("PUT");
			return request;
		}).contentType(MediaType.MULTIPART_FORM_DATA)).andExpect(status().isNotFound());
	}

	// deleteRolSaga

	@Test
	void should_delete_rol_saga() throws Exception {
		mockMvc.perform(delete(BASE_URL + "/{id}", rolSagaResponse.getId())).andExpect(status().isNoContent());

		verify(rolSagaService).delete(rolSagaResponse.getId());
	}

}
