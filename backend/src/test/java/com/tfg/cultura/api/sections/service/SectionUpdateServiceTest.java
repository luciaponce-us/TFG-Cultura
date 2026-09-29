package com.tfg.cultura.api.sections.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.core.factory.ExceptionsFactory;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.sections.service.specifications.*;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class SectionUpdateServiceTest {

	@Mock
	private SectionRepository sectionRepository;

	@Mock
	private UserService userService;

	@Mock
	private SectionService sectionService;

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
	private SectionUpdateService sectionUpdateService;

	private Section section;
	private SectionCreateRequest sectionCreateRequest;
	private Set<String> managerUsernames;
	private Set<User> managers;
	private Set<User> collaborators;
	private User manager;
	private String managerUsername;
	private User collaborator;
	private String collaboratorUsername;
	private String sectionId;

	@BeforeEach
	void setup() {
		section = SectionFactory.validSection();
		sectionCreateRequest = SectionFactory.validSectionCreateRequest(section);
		managerUsernames = sectionCreateRequest.getManagersUsernames();
		managers = section.getManagers();
		collaborators = section.getCollaborators();
		manager = managers.stream().findFirst().get();
		managerUsername = manager.getUsername();
		collaborator = collaborators.stream().findFirst().get();
		collaboratorUsername = collaborator.getUsername();
		sectionId = section.getId();
	}

	// ====================== UPDATE ======================

	// ✅​ 200 - OK
	@Test
	void should_update_section() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);
		SectionCreateRequest request = SectionFactory.validSectionCreateRequest(section);

		when(sectionRepository.save(any(Section.class))).thenAnswer(invocation -> invocation.getArgument(0));

		SectionResponse response = sectionUpdateService.updateSection(sectionId, request);

		assertEquals(request.getName(), response.getName());

		verify(uniqueSectionNameSpecification).validateForUpdate(request.getName(), sectionId);
		verify(sectionRepository).save(section);
	}

	// ❌​ 404 - Not Found
	@Test
	void should_throw_when_section_not_found() {
		doThrow(ExceptionsFactory.notFoundException).when(sectionService).findSectionById(sectionId);

		assertThrows(NotFoundException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 409 - Conflict - Section Already Exists
	@Test
	void should_throw_when_section_name_already_exists() {

		doThrow(ExceptionsFactory.duplicationException("name")).when(uniqueSectionNameSpecification)
				.validateForUpdate(sectionCreateRequest.getName(), sectionId);

		assertThrows(DuplicationException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 400 - Bad Request - Invalid Manager Role
	@Test
	void should_throw_when_manager_has_invalid_role() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		doThrow(ExceptionsFactory.validationException("managers")).when(sectionService).setSectionManagers(section,
				managerUsernames);

		assertThrows(ValidationException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 409 - Conflict - Manager Already Assigned
	@Test
	void should_throw_when_manager_is_already_assigned() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "managers")).when(sectionService)
				.setSectionManagers(section, managerUsernames);

		assertThrows(FieldException.class, () -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== REMOVE MANAGER ======================

	// ✅​ 200 - OK
	@Test
	void should_remove_manager_from_section() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(managerUsername)).thenReturn(manager);

		when(sectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.removeManagerFromSection(sectionId, managerUsername);

		assertFalse(section.getManagers().contains(manager));
		assertEquals(section.getName(), response.getName());

		verify(sectionRepository).save(section);
	}

	// ❌​ 404 - Not Found
	@Test
	void should_throw_when_section_not_found_remove_manager() {
		doThrow(ExceptionsFactory.notFoundException).when(sectionService).findSectionById(sectionId);

		assertThrows(NotFoundException.class,
				() -> sectionUpdateService.removeManagerFromSection(sectionId, managerUsername));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 404 - Not Found - User Not Found
	@Test
	void should_throw_when_manager_not_found_remove_manager() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(managerUsername)).thenThrow(new UserNotFoundException("error"));

		assertThrows(UserNotFoundException.class,
				() -> sectionUpdateService.removeManagerFromSection(sectionId, managerUsername));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 404 - Not Found - User Not Found (Manager not in section)
	@Test
	void should_throw_when_user_is_not_manager_of_section() throws Exception {
		User managerToRemove = User.builder().username("not-in-section").build();

		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername("not-in-section")).thenReturn(managerToRemove);

		assertThrows(UserNotFoundException.class,
				() -> sectionUpdateService.removeManagerFromSection(sectionId, "not-in-section"));
		verify(sectionRepository, never()).save(any());
	}

	// ====================== REMOVE COLLABORATOR ======================

	// ✅ 200 - OK
	@Test
	void should_remove_collaborator_from_section() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(collaboratorUsername)).thenReturn(collaborator);

		when(sectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.removeCollaboratorFromSection(sectionId, collaboratorUsername);

		assertFalse(section.getCollaborators().contains(collaborator));
		assertEquals(section.getName(), response.getName());

		verify(sectionRepository).save(section);
	}

	// ❌ 404 - Not Found
	@Test
	void should_throw_when_section_not_found_remove_collaborator() {
		doThrow(ExceptionsFactory.notFoundException).when(sectionService).findSectionById(sectionId);

		assertThrows(NotFoundException.class,
				() -> sectionUpdateService.removeCollaboratorFromSection(sectionId, collaboratorUsername));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found
	@Test
	void should_throw_when_collaborator_not_found_remove_collaborator() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(collaboratorUsername)).thenThrow(new UserNotFoundException("error"));

		assertThrows(UserNotFoundException.class,
				() -> sectionUpdateService.removeCollaboratorFromSection(sectionId, collaboratorUsername));

		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found (Collaborator not in section)
	@Test
	void should_throw_when_user_is_not_collaborator_of_section() throws Exception {
		User collaboratorToRemove = User.builder().username("otherCollaborator").build();

		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername("otherCollaborator")).thenReturn(collaboratorToRemove);

		assertThrows(UserNotFoundException.class,
				() -> sectionUpdateService.removeCollaboratorFromSection(sectionId, "otherCollaborator"));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== ADD MANAGER ======================

	// ✅ 200 - OK
	@Test
	void should_add_manager_to_section() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(managerUsername)).thenReturn(manager);

		when(sectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.addManagerToSection(sectionId, managerUsername);

		assertTrue(section.getManagers().contains(manager));
		assertEquals(section.getName(), response.getName());

		verify(managersMustBeEncargadosSpecification).validate(Set.of(manager));
		verify(singleSectionManagerSpecification).validate(Set.of(manager), sectionId);
		verify(sectionRepository).save(section);
	}

	// ❌ 404 - Section Not Found
	@Test
	void should_throw_when_section_not_found_add_manager() {
		doThrow(ExceptionsFactory.notFoundException).when(sectionService).findSectionById(sectionId);

		assertThrows(NotFoundException.class,
				() -> sectionUpdateService.addManagerToSection(sectionId, managerUsername));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found
	@Test
	void should_throw_when_manager_not_found_add_manager() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(managerUsername)).thenThrow(new UserNotFoundException("error"));

		assertThrows(UserNotFoundException.class,
				() -> sectionUpdateService.addManagerToSection(sectionId, managerUsername));

		verify(sectionRepository, never()).save(any());
	}

	// ❌ 400 - Invalid Manager Role
	@Test
	void should_throw_when_manager_has_invalid_role_add_manager() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(managerUsername)).thenReturn(manager);

		doThrow(ExceptionsFactory.validationException("managers")).when(managersMustBeEncargadosSpecification)
				.validate(Set.of(manager));

		assertThrows(ValidationException.class,
				() -> sectionUpdateService.addManagerToSection(sectionId, managerUsername));

		verify(singleSectionManagerSpecification, never()).validate(anySet(), anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 409 - Manager Already Assigned
	@Test
	void should_throw_when_manager_already_assigned_to_other_section() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(managerUsername)).thenReturn(manager);

		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "managers"))
				.when(singleSectionManagerSpecification).validate(Set.of(manager), sectionId);

		assertThrows(FieldException.class, () -> sectionUpdateService.addManagerToSection(sectionId, managerUsername));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== ADD COLLABORATOR ======================

	// ✅ 200 - OK
	@Test
	void should_add_collaborator_to_section() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(collaboratorUsername)).thenReturn(collaborator);

		when(sectionRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.addCollaboratorToSection(sectionId, collaboratorUsername);

		assertTrue(section.getCollaborators().contains(collaborator));
		assertEquals(section.getName(), response.getName());

		verify(collaboratorsMustBeColaboradoresSpecification).validate(Set.of(collaborator));
		verify(singleSectionCollaboratorSpecification).validate(Set.of(collaborator), sectionId);
		verify(sectionRepository).save(section);
	}

	// ❌ 404 - Section Not Found
	@Test
	void should_throw_when_section_not_found_add_collaborator() {
		doThrow(ExceptionsFactory.notFoundException).when(sectionService).findSectionById(sectionId);

		assertThrows(NotFoundException.class,
				() -> sectionUpdateService.addCollaboratorToSection(sectionId, collaboratorUsername));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found
	@Test
	void should_throw_when_collaborator_not_found_add_collaborator() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(collaboratorUsername)).thenThrow(new UserNotFoundException("error"));

		assertThrows(UserNotFoundException.class,
				() -> sectionUpdateService.addCollaboratorToSection(sectionId, collaboratorUsername));

		verify(sectionRepository, never()).save(any());
	}

	// ❌ 400 - Invalid Collaborator Role
	@Test
	void should_throw_when_collaborator_has_invalid_role_add_collaborator() {
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(collaboratorUsername)).thenReturn(collaborator);

		doThrow(ExceptionsFactory.validationException("collaborators"))
				.when(collaboratorsMustBeColaboradoresSpecification).validate(Set.of(collaborator));

		assertThrows(ValidationException.class,
				() -> sectionUpdateService.addCollaboratorToSection(sectionId, collaboratorUsername));

		verify(singleSectionCollaboratorSpecification, never()).validate(anySet(), anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 409 - Collaborator Already Assigned
	@Test
	void should_throw_when_collaborator_already_assigned_to_other_section() {
		FieldException fieldException = new FieldException(LoggerFactory.getLogger("testLogger"), HttpStatus.CONFLICT,
				Map.of("collaborators", "collaborator already assigned to another section"));
		when(sectionService.findSectionById(sectionId)).thenReturn(section);

		when(userService.findUserByUsername(collaboratorUsername)).thenReturn(collaborator);

		doThrow(fieldException).when(singleSectionCollaboratorSpecification).validate(Set.of(collaborator), sectionId);

		assertThrows(FieldException.class,
				() -> sectionUpdateService.addCollaboratorToSection(sectionId, collaboratorUsername));

		verify(sectionRepository, never()).save(any());
	}

}
