package com.tfg.cultura.api.sections.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.cultura.api.sections.exception.*;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.service.SectionService;
import com.tfg.cultura.api.utils.BaseControllerTest;

public class SectionControllerTest extends BaseControllerTest {

    @Mock
    private SectionService sectionService;

    private static final String BASE_URL = "/api/sections";

    private Section section;
    private SectionCreateRequest sectionCreateRequest;
    private SectionResponse sectionResponse;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SectionController controller = new SectionController(sectionService);
        mockMvc = buildMockMvc(controller, SectionExceptionHandler.class);

        initTestData();
    }

    void initTestData() {
        section = SectionFactory.validSection();
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
                        sectionCreateRequest.getManagersUsernames().getFirst()));

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
                        sectionCreateRequest.getCollaboratorsUsernames().getFirst()));

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
                        sectionCreateRequest.getManagersUsernames().getFirst()));

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
                        sectionCreateRequest.getCollaboratorsUsernames().getFirst()));

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

}
