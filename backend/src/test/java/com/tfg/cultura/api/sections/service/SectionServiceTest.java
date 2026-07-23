package com.tfg.cultura.api.sections.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.sections.exception.*;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.sections.service.specifications.*;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.users.service.UserService;

@ExtendWith(MockitoExtension.class)
public class SectionServiceTest {
	@Mock
	private SectionRepository sectionRepository;

	@Mock
	private UserService userService;

	// SPECIFICATIONS - BUSINESS RULES
	@Mock
	private UniqueSectionNameSpecification uniqueSectionNameSpecification;
	@Mock
	private ManagersMustBeEncargadosSpecification managersMustBeEncargadosSpecification;
	@Mock
	private SingleSectionManagerSpecification singleSectionManagerSpecification;
	@Mock
	private CollaboratorsMustBeColaboradoresSpecification collaboratorsMustBeColaboradoresSpecification;
	@Mock
	private SingleSectionCollaboratorSpecification singleSectionCollaboratorSpecification;

	@InjectMocks
	private SectionService sectionService;

	private Section section;
	private SectionCreateRequest sectionCreateRequest;

	@BeforeEach
	void setup() {
		section = SectionFactory.validSection();
		sectionCreateRequest = SectionFactory.validSectionCreateRequest(section);
	}

	// ====================== HELPERS ======================

	void mockExistingUsersWithRoles(Role managerRole, Role collaboratorRole) {
		User manager = section.getManagers().stream()
				.findFirst().get();
		manager.setRole(managerRole);
		section.setManagers(Set.of(manager));
		User collaborator = section.getCollaborators().stream()
				.findFirst().get();
		collaborator.setRole(collaboratorRole);
		section.setCollaborators(Set.of(collaborator));
	}

	// ====================== CREATION ======================

	// ✅​ 201 - Created
	@Test
	void shouldCreateSectionSuccessfully() {
		// Arrange
		Set<User> managers = section.getManagers();
		User manager = managers.stream()
				.findFirst().get();
		Set<User> collaborators = section.getCollaborators();
		User collaborator = collaborators.stream()
				.findFirst().get();

		mockExistingUsersWithRoles(Role.ENCARGADO, Role.COLABORADOR);
		when(userService.findUsersByUsernames(Set.of(manager.getUsername())))
				.thenReturn(Set.of(manager));
		when(userService.findUsersByUsernames(Set.of(collaborator.getUsername())))
				.thenReturn(Set.of(collaborator));
		when(sectionRepository.save(any(Section.class))).thenReturn(section);
		assertEquals(1, sectionCreateRequest.getManagersUsernames().size());

		// Act
		SectionResponse response = sectionService.createSection(sectionCreateRequest);

		// Assert
		assertEquals(sectionCreateRequest.getName(), response.getName());
		assertEquals(managers.size(), response.getManagers().size());
		assertEquals(collaborators.size(), response.getCollaborators().size());
		assertTrue(response.getManagers().stream()
				.anyMatch(m -> m.getUsername().equals(manager.getUsername())));
		assertTrue(response.getCollaborators().stream()
				.anyMatch(c -> c.getUsername().equals(collaborator.getUsername())));

		verify(uniqueSectionNameSpecification).validate(sectionCreateRequest.getName());
		verify(managersMustBeEncargadosSpecification).validate(managers);
		verify(singleSectionManagerSpecification).validate(managers, null);
		verify(collaboratorsMustBeColaboradoresSpecification).validate(collaborators);
		verify(singleSectionCollaboratorSpecification).validate(collaborators, null);

		verify(sectionRepository).save(any(Section.class));
	}

	// ❌​ 409 - Conflict - Section Already Exists
	@Test
	void should_throw_when_section_name_already_exists() {
		doThrow(new SectionAlreadyExistsException("error"))
				.when(uniqueSectionNameSpecification)
				.validate(sectionCreateRequest.getName());

		assertThrows(
				SectionAlreadyExistsException.class,
				() -> sectionService.createSection(sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 400 - Bad Request - Invalid Manager Role
	@Test
	void should_throw_when_manager_has_invalid_role() {
		mockExistingUsersWithRoles(Role.SOCIO, Role.COLABORADOR); // Manager has invalid role

		doThrow(new InvalidManagerRoleException("error"))
				.when(managersMustBeEncargadosSpecification)
				.validate(anySet());

		assertThrows(
				InvalidManagerRoleException.class,
				() -> sectionService.createSection(sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 409 - Conflict - Manager Already Assigned
	@Test
	void should_throw_when_manager_is_already_assigned() {
		doThrow(new ManagerAlreadyAssignedException("error"))
				.when(singleSectionManagerSpecification)
				.validate(anySet(), isNull());

		assertThrows(
				ManagerAlreadyAssignedException.class,
				() -> sectionService.createSection(sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 400 - Bad Request - Invalid Collaborator Role
	@Test
	void should_throw_when_collaborator_has_invalid_role() {
		mockExistingUsersWithRoles(Role.ENCARGADO, Role.SOCIO); // Collaborator has invalid role

		doThrow(new InvalidCollaboratorRoleException("error"))
				.when(collaboratorsMustBeColaboradoresSpecification)
				.validate(anySet());

		assertThrows(
				InvalidCollaboratorRoleException.class,
				() -> sectionService.createSection(sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 409 - Conflict - Collaborator Already Assigned
	@Test
	void should_throw_when_collaborator_is_already_assigned() {
		mockExistingUsersWithRoles(Role.ENCARGADO, Role.COLABORADOR);

		doThrow(new CollaboratorAlreadyAssignedException("error"))
				.when(singleSectionCollaboratorSpecification)
				.validate(anySet(), isNull());

		assertThrows(
				CollaboratorAlreadyAssignedException.class,
				() -> sectionService.createSection(sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== GET ALL SECTIONS ======================

	@Test
	void should_return_all_sections_when_name_filter_is_null() {
		// Arrange
		List<Section> sections = List.of(
				Section.builder().name("Manga").build(),
				Section.builder().name("Videojuegos").build());

		when(sectionRepository.findAll()).thenReturn(sections);

		// Act
		List<SectionResponse> result = sectionService.getAllSections(null);

		// Assert
		assertEquals(2, result.size());
		assertEquals("Manga", result.get(0).getName());
		assertEquals("Videojuegos", result.get(1).getName());

		verify(sectionRepository).findAll();
		verify(sectionRepository, never()).findAllByNameContainingIgnoreCase(anyString());
	}

	@Test
	void should_return_all_sections_when_name_filter_is_empty() {
		// Arrange
		List<Section> sections = List.of(
				Section.builder().name("Libros").build());

		when(sectionRepository.findAll()).thenReturn(sections);

		// Act
		List<SectionResponse> result = sectionService.getAllSections("");

		// Assert
		assertEquals(1, result.size());
		assertEquals("Libros", result.get(0).getName());

		verify(sectionRepository).findAll();
		verify(sectionRepository, never()).findAllByNameContainingIgnoreCase(anyString());
	}

	@Test
	void should_return_filtered_sections_when_name_filter_is_provided() {
		// Arrange
		List<Section> sections = List.of(
				Section.builder().name("Videojuegos").build());

		when(sectionRepository.findAllByNameContainingIgnoreCase("video"))
				.thenReturn(sections);

		// Act
		List<SectionResponse> result = sectionService.getAllSections("video");

		// Assert
		assertEquals(1, result.size());
		assertEquals("Videojuegos", result.get(0).getName());

		verify(sectionRepository).findAllByNameContainingIgnoreCase("video");
		verify(sectionRepository, never()).findAll();
	}

	// ====================== GET BY ID ======================

	// ✅​ 200 - OK
	@Test
	void should_return_section_when_section_exists() throws SectionNotFoundException {
		// Arrange
		when(sectionRepository.findById(section.getId()))
				.thenReturn(Optional.of(section));

		// Act
		SectionResponse response = sectionService.getSectionById(section.getId());

		// Assert
		assertNotNull(response);
		assertEquals(section.getId(), response.getId());
		assertEquals(section.getName(), response.getName());

		verify(sectionRepository).findById(section.getId());
	}

	// ❌​ 404 - Not Found
	@Test
	void should_throw_exception_when_section_does_not_exist() {
		// Arrange
		String id = "non-existent-id";

		when(sectionRepository.findById(id))
				.thenReturn(Optional.empty());

		// Act & Assert
		SectionNotFoundException exception = assertThrows(
				SectionNotFoundException.class,
				() -> sectionService.getSectionById(id));

		assertEquals("Sección no encontrada con ID: " + id, exception.getMessage());

		verify(sectionRepository).findById(id);
	}

}
