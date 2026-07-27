package com.tfg.cultura.api.sections.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.cultura.api.sections.exception.*;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.service.SectionService;
import com.tfg.cultura.api.sections.service.SectionUpdateService;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.exception.UsersExceptionHandler;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.utils.BaseControllerTest;

public class SectionControllerTest extends BaseControllerTest {

	@Mock
	private SectionService sectionService;

	@Mock
	private SectionUpdateService sectionUpdateService;

	private static final String BASE_URL = "/api/sections";
	private static final String SECTION_URL = BASE_URL + "/{id}";
	private static final String SECTION_REMOVE_MANAGER_URL = BASE_URL +
			"/{id}/managers/{managerUsername}/remove";
	private static final String SECTION_REMOVE_COLLABORATOR_URL = BASE_URL +
			"/{id}/collaborators/{collaboratorUsername}/remove";
	private static final String SECTION_ADD_MANAGER_URL = BASE_URL +
			"/{id}/managers/{managerUsername}/add";
	private static final String SECTION_ADD_COLLABORATOR_URL = BASE_URL +
			"/{id}/collaborators/{collaboratorUsername}/add";

	private Section section;
	private User manager;
	private User collaborator;
	private SectionCreateRequest sectionCreateRequest;
	private SectionResponse sectionResponse;
	private ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		SectionController controller = new SectionController(sectionService, sectionUpdateService);
		mockMvc = buildMockMvc(controller, SectionExceptionHandler.class, UsersExceptionHandler.class);

		initTestData();
	}

	void initTestData() {
		section = SectionFactory.validSection();
		manager = section.getManagers().stream().findFirst().get();
		collaborator = section.getCollaborators().stream().findFirst().get();
		sectionCreateRequest = SectionFactory.validSectionCreateRequest(section);
		sectionResponse = new SectionResponse(section);
	}

	// ====================== CREATION ======================

	// ✅​ 201 - Created
	@Test
	void should_create_section() throws Exception {
		when(sectionService.createSection(any(SectionCreateRequest.class)))
				.thenReturn(sectionResponse);

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name").value(sectionCreateRequest.getName()));

		verify(sectionService).createSection(any(SectionCreateRequest.class));
	}

	// ❌​ 409 - Conflict
	@Test
	void should_return_conflict_when_section_already_exists() throws Exception {
		when(sectionService.createSection(any(SectionCreateRequest.class)))
				.thenThrow(new SectionAlreadyExistsException(sectionCreateRequest.getName()));

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("Section Already Exists"));

		verify(sectionService).createSection(any(SectionCreateRequest.class));
	}

	// ❌​ 400 - Bad Request - Invalid Manager Role
	@Test
	void should_return_bad_request_when_manager_role_is_invalid() throws Exception {
		when(sectionService.createSection(any()))
				.thenThrow(new InvalidManagerRoleException(
						sectionCreateRequest.getManagersUsernames().stream()
								.findFirst().get()));

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid Manager Role"));

		verify(sectionService).createSection(any());
	}

	// ❌​ 400 - Bad Request - Invalid Collaborator Role
	@Test
	void should_return_bad_request_when_collaborator_role_is_invalid() throws Exception {
		when(sectionService.createSection(any()))
				.thenThrow(new InvalidCollaboratorRoleException(
						sectionCreateRequest.getCollaboratorsUsernames().stream()
								.findFirst().get()));

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").value("Invalid Collaborator Role"));

		verify(sectionService).createSection(any());
	}

	// ❌​ 409 - Conflict - Manager Already Assigned
	@Test
	void should_return_conflict_when_manager_already_assigned() throws Exception {
		when(sectionService.createSection(any()))
				.thenThrow(new ManagerAlreadyAssignedException(
						sectionCreateRequest.getManagersUsernames().stream()
								.findFirst().get()));

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error")
						.value("Manager Already Assigned to Another Section"));

		verify(sectionService).createSection(any());
	}

	// ❌​ 409 - Conflict - Collaborator Already Assigned
	@Test
	void should_return_conflict_when_collaborator_already_assigned() throws Exception {
		when(sectionService.createSection(any()))
				.thenThrow(new CollaboratorAlreadyAssignedException(
						sectionCreateRequest.getCollaboratorsUsernames().stream()
								.findFirst().get()));

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error")
						.value("Collaborator Already Assigned to Another Section"));

		verify(sectionService).createSection(any());
	}

	// ❌​ 400 - Bad Request - Invalid Request Body
	@Test
	void should_return_bad_request_when_request_is_invalid() throws Exception {
		sectionCreateRequest.setName(""); // Invalid name

		mockMvc.perform(post(BASE_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(sectionService);
	}

	// ====================== GET ALL ======================

	// ✅​ 200 - OK - Get all sections without name filter
	@Test
	void should_get_all_sections_when_name_filter_is_not_provided() throws Exception {
		// Arrange
		when(sectionService.getAllSections(null))
				.thenReturn(List.of(sectionResponse));

		// Act & Assert
		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].name").value(section.getName()));

		verify(sectionService).getAllSections(null);
	}

	// ✅​ 200 - OK - Get filtered sections when name filter is provided
	@Test
	void should_get_filtered_sections_when_name_filter_is_provided() throws Exception {
		// Arrange
		when(sectionService.getAllSections("manga"))
				.thenReturn(List.of(sectionResponse));

		// Act & Assert
		mockMvc.perform(get(BASE_URL)
				.param("nameFilter", "manga"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].name").value(section.getName()));

		verify(sectionService).getAllSections("manga");
	}

	// ✅​ 200 - OK - Get all sections when no sections are found
	@Test
	void should_return_empty_list_when_no_sections_are_found() throws Exception {
		// Arrange
		when(sectionService.getAllSections(null))
				.thenReturn(Collections.emptyList());

		// Act & Assert
		mockMvc.perform(get(BASE_URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(0));

		verify(sectionService).getAllSections(null);
	}

	// ====================== GET BY ID ======================

	// ✅​ 200 - OK - Get section by ID when section exists
	@Test
	void should_return_section_when_get_section_by_id() throws Exception {
		// Arrange
		when(sectionService.getSectionById(section.getId()))
				.thenReturn(sectionResponse);

		// Act & Assert
		mockMvc.perform(get(SECTION_URL, section.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(section.getId()))
				.andExpect(jsonPath("$.name").value(section.getName()));

		verify(sectionService).getSectionById(section.getId());
	}

	// ❌​ 404 - Not Found - Get section by ID when section does not exist
	@Test
	void should_return_not_found_when_section_does_not_exist() throws Exception {
		// Arrange
		when(sectionService.getSectionById(section.getId()))
				.thenThrow(new SectionNotFoundException(
						"Sección no encontrada con ID: " + section.getId()));

		// Act & Assert
		mockMvc.perform(get(SECTION_URL, section.getId()))
				.andExpect(status().isNotFound());

		verify(sectionService).getSectionById(section.getId());
	}

	// ====================== UPDATE ======================

	// ✅ 200 - OK
	@Test
	void should_update_section() throws Exception {
		when(sectionUpdateService.updateSection(eq(section.getId()), any(SectionCreateRequest.class)))
				.thenReturn(sectionResponse);

		mockMvc.perform(put(SECTION_URL, section.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(sectionResponse.getName()));

		verify(sectionUpdateService).updateSection(eq(section.getId()), any(SectionCreateRequest.class));
	}

	// ❌ 404 - Section Not Found
	@Test
	void should_return_not_found_when_updating_non_existing_section() throws Exception {
		when(sectionUpdateService.updateSection(eq(section.getId()), any(SectionCreateRequest.class)))
				.thenThrow(new SectionNotFoundException("Section not found"));

		mockMvc.perform(put(SECTION_URL, section.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).updateSection(eq(section.getId()), any(SectionCreateRequest.class));
	}

	// ❌ 409 - Conflict
	@Test
	void should_return_conflict_when_section_name_already_exists() throws Exception {
		when(sectionUpdateService.updateSection(eq(section.getId()), any(SectionCreateRequest.class)))
				.thenThrow(new SectionAlreadyExistsException("Section already exists"));

		mockMvc.perform(put(SECTION_URL, section.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(sectionCreateRequest)))
				.andExpect(status().isConflict());

		verify(sectionUpdateService).updateSection(eq(section.getId()), any(SectionCreateRequest.class));
	}

	// ❌ 400 - Bad Request
	@Test
	void should_return_bad_request_when_update_request_is_invalid() throws Exception {
		SectionCreateRequest invalidRequest = new SectionCreateRequest(
				"",
				Set.of(),
				Set.of());

		mockMvc.perform(put(SECTION_URL, section.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(sectionUpdateService);
	}

	// ====================== REMOVE MANAGER ======================

	// ✅ 200 - OK
	@Test
	void should_remove_manager_from_section() throws Exception {
		when(sectionUpdateService.removeManagerFromSection(section.getId(), manager.getUsername()))
				.thenReturn(sectionResponse);

		mockMvc.perform(put(SECTION_REMOVE_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(sectionResponse.getId()))
				.andExpect(jsonPath("$.name").value(sectionResponse.getName()));

		verify(sectionUpdateService).removeManagerFromSection(
				section.getId(),
				manager.getUsername());
	}

	// ❌ 404 - Not Found - Section not found
	@Test
	void should_return_not_found_when_deleting_manager_from_non_existing_section() throws Exception {
		when(sectionUpdateService.removeManagerFromSection(section.getId(), manager.getUsername()))
				.thenThrow(new SectionNotFoundException("error"));

		mockMvc.perform(put(SECTION_REMOVE_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).removeManagerFromSection(
				section.getId(),
				manager.getUsername());
	}

	// ❌ 404 - Not Found - Manager not found in section
	@Test
	void should_return_not_found_when_manager_does_not_exist() throws Exception {
		when(sectionUpdateService.removeManagerFromSection(section.getId(), manager.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		mockMvc.perform(put(SECTION_REMOVE_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).removeManagerFromSection(
				section.getId(),
				manager.getUsername());
	}

	// ======================= REMOVE COLLABORATOR ======================

	// ✅ 200 - OK
	@Test
	void should_remove_collaborator_from_section() throws Exception {
		when(sectionUpdateService.removeCollaboratorFromSection(section.getId(), collaborator.getUsername()))
				.thenReturn(sectionResponse);

		mockMvc.perform(put(SECTION_REMOVE_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(sectionResponse.getId()))
				.andExpect(jsonPath("$.name").value(sectionResponse.getName()));

		verify(sectionUpdateService).removeCollaboratorFromSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ❌ 404 - Not Found - Section not found
	@Test
	void should_return_not_found_when_deleting_collaborator_from_non_existing_section() throws Exception {
		when(sectionUpdateService.removeCollaboratorFromSection(section.getId(), collaborator.getUsername()))
				.thenThrow(new SectionNotFoundException("error"));

		mockMvc.perform(put(SECTION_REMOVE_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).removeCollaboratorFromSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ❌ 404 - Not Found - Collaborator not found in section
	@Test
	void should_return_not_found_when_collaborator_does_not_exist() throws Exception {
		when(sectionUpdateService.removeCollaboratorFromSection(section.getId(), collaborator.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		mockMvc.perform(put(SECTION_REMOVE_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).removeCollaboratorFromSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ====================== ADD MANAGER ======================

	// ✅ 200 - OK
	@Test
	void should_add_manager_to_section() throws Exception {
		when(sectionUpdateService.addManagerToSection(section.getId(), manager.getUsername()))
				.thenReturn(sectionResponse);

		mockMvc.perform(put(SECTION_ADD_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(sectionResponse.getId()))
				.andExpect(jsonPath("$.name").value(sectionResponse.getName()));

		verify(sectionUpdateService).addManagerToSection(
				section.getId(),
				manager.getUsername());
	}

	// ❌ 404 - Not Found - Section not found
	@Test
	void should_return_not_found_when_adding_manager_to_non_existing_section() throws Exception {
		when(sectionUpdateService.addManagerToSection(section.getId(), manager.getUsername()))
				.thenThrow(new SectionNotFoundException("error"));

		mockMvc.perform(put(SECTION_ADD_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).addManagerToSection(
				section.getId(),
				manager.getUsername());
	}

	// ❌ 404 - Not Found - User not found
	@Test
	void should_return_not_found_when_manager_to_add_does_not_exist() throws Exception {
		when(sectionUpdateService.addManagerToSection(section.getId(), manager.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		mockMvc.perform(put(SECTION_ADD_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).addManagerToSection(
				section.getId(),
				manager.getUsername());
	}

	// ❌ 400 - Bad Request - Invalid manager role
	@Test
	void should_return_bad_request_when_manager_has_invalid_role() throws Exception {
		when(sectionUpdateService.addManagerToSection(section.getId(), manager.getUsername()))
				.thenThrow(new InvalidManagerRoleException("error"));

		mockMvc.perform(put(SECTION_ADD_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isBadRequest());

		verify(sectionUpdateService).addManagerToSection(
				section.getId(),
				manager.getUsername());
	}

	// ❌ 409 - Conflict - Manager already assigned
	@Test
	void should_return_conflict_when_manager_is_already_assigned() throws Exception {
		when(sectionUpdateService.addManagerToSection(section.getId(), manager.getUsername()))
				.thenThrow(new ManagerAlreadyAssignedException("error"));

		mockMvc.perform(put(SECTION_ADD_MANAGER_URL,
				section.getId(),
				manager.getUsername()))
				.andExpect(status().isConflict());

		verify(sectionUpdateService).addManagerToSection(
				section.getId(),
				manager.getUsername());
	}

	// ====================== ADD COLLABORATOR ======================

	// ✅ 200 - OK
	@Test
	void should_add_collaborator_to_section() throws Exception {
		when(sectionUpdateService.addCollaboratorToSection(section.getId(), collaborator.getUsername()))
				.thenReturn(sectionResponse);

		mockMvc.perform(put(SECTION_ADD_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(sectionResponse.getId()))
				.andExpect(jsonPath("$.name").value(sectionResponse.getName()));

		verify(sectionUpdateService).addCollaboratorToSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ❌ 404 - Not Found - Section not found
	@Test
	void should_return_not_found_when_adding_collaborator_to_non_existing_section() throws Exception {
		when(sectionUpdateService.addCollaboratorToSection(section.getId(), collaborator.getUsername()))
				.thenThrow(new SectionNotFoundException("error"));

		mockMvc.perform(put(SECTION_ADD_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).addCollaboratorToSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ❌ 404 - Not Found - User not found
	@Test
	void should_return_not_found_when_collaborator_to_add_does_not_exist() throws Exception {
		when(sectionUpdateService.addCollaboratorToSection(section.getId(), collaborator.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		mockMvc.perform(put(SECTION_ADD_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isNotFound());

		verify(sectionUpdateService).addCollaboratorToSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ❌ 400 - Bad Request - Invalid collaborator role
	@Test
	void should_return_bad_request_when_collaborator_has_invalid_role() throws Exception {
		when(sectionUpdateService.addCollaboratorToSection(section.getId(), collaborator.getUsername()))
				.thenThrow(new InvalidCollaboratorRoleException("error"));

		mockMvc.perform(put(SECTION_ADD_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isBadRequest());

		verify(sectionUpdateService).addCollaboratorToSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ❌ 409 - Conflict - Collaborator already assigned
	@Test
	void should_return_conflict_when_collaborator_is_already_assigned() throws Exception {
		when(sectionUpdateService.addCollaboratorToSection(section.getId(), collaborator.getUsername()))
				.thenThrow(new CollaboratorAlreadyAssignedException("error"));

		mockMvc.perform(put(SECTION_ADD_COLLABORATOR_URL,
				section.getId(),
				collaborator.getUsername()))
				.andExpect(status().isConflict());

		verify(sectionUpdateService).addCollaboratorToSection(
				section.getId(),
				collaborator.getUsername());
	}

	// ====================== DELETE ======================

	// ✅ 204 - No Content
	@Test
	void should_delete_section() throws Exception {
		doNothing().when(sectionService).deleteSection(section.getId());

		mockMvc.perform(delete(SECTION_URL, section.getId()))
				.andExpect(status().isNoContent());

		verify(sectionService).deleteSection(section.getId());
	}

	// ❌ 404 - Not Found - Section not found
	@Test
	void should_return_not_found_when_deleting_non_existing_section() throws Exception {
		doThrow(new SectionNotFoundException("error"))
				.when(sectionService)
				.deleteSection(section.getId());

		mockMvc.perform(delete(SECTION_URL, section.getId()))
				.andExpect(status().isNotFound());

		verify(sectionService).deleteSection(section.getId());
	}

}
