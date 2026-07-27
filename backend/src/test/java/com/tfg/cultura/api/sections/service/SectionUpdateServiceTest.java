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

import java.util.HashSet;
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
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;

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
	private User collaborator;
	private String sectionId;

	@BeforeEach
	void setup() {
		section = SectionFactory.validSection();
		sectionCreateRequest = SectionFactory.validSectionCreateRequest(section);
		managerUsernames = sectionCreateRequest.getManagersUsernames();
		managers = section.getManagers();
		collaborators = section.getCollaborators();
		manager = managers.stream().findFirst().get();
		collaborator = collaborators.stream().findFirst().get();
		sectionId = section.getId();
	}

	// ====================== UPDATE ======================

	// ✅​ 200 - OK
	@Test
	void should_update_section() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);
		SectionCreateRequest request = SectionFactory.validSectionCreateRequest(section);

		when(sectionRepository.save(any(Section.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		SectionResponse response = sectionUpdateService.updateSection(sectionId, request);

		assertEquals(request.getName(), response.getName());

		verify(uniqueSectionNameSpecification)
				.validateForUpdate(request.getName(), sectionId);
		verify(sectionRepository).save(section);
	}

	// ❌​ 404 - Not Found
	@Test
	void should_throw_when_section_not_found() {
		doThrow(new SectionNotFoundException("error"))
				.when(sectionService)
				.findSectionById(sectionId);

		assertThrows(
				SectionNotFoundException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 409 - Conflict - Section Already Exists
	@Test
	void should_throw_when_section_name_already_exists() {

		doThrow(new SectionAlreadyExistsException("error"))
				.when(uniqueSectionNameSpecification)
				.validateForUpdate(sectionCreateRequest.getName(), sectionId);

		assertThrows(
				SectionAlreadyExistsException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 400 - Bad Request - Invalid Manager Role
	@Test
	void should_throw_when_manager_has_invalid_role() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		doThrow(new InvalidManagerRoleException("error"))
				.when(sectionService)
				.setSectionManagers(section, managerUsernames);

		assertThrows(
				InvalidManagerRoleException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 409 - Conflict - Manager Already Assigned
	@Test
	void should_throw_when_manager_is_already_assigned() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		doThrow(new ManagerAlreadyAssignedException("error"))
				.when(sectionService)
				.setSectionManagers(section, managerUsernames);

		assertThrows(
				ManagerAlreadyAssignedException.class,
				() -> sectionUpdateService.updateSection(sectionId, sectionCreateRequest));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== REMOVE MANAGER ======================

	// ✅​ 200 - OK
	@Test
	void should_remove_manager_from_section() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(manager.getUsername()))
				.thenReturn(manager);

		when(sectionRepository.save(any()))
				.thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.removeManagerFromSection(sectionId, manager.getUsername());

		assertFalse(section.getManagers().contains(manager));
		assertEquals(section.getName(), response.getName());

		verify(sectionRepository).save(section);
	}

	// ❌​ 404 - Not Found
	@Test
	void should_throw_when_section_not_found_remove_manager() {
		doThrow(new SectionNotFoundException("error"))
				.when(sectionService)
				.findSectionById(sectionId);

		assertThrows(
				SectionNotFoundException.class,
				() -> sectionUpdateService.removeManagerFromSection(sectionId, manager.getUsername()));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 404 - Not Found - User Not Found
	@Test
	void should_throw_when_manager_not_found_remove_manager() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(manager.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		assertThrows(
				UserNotFoundException.class,
				() -> sectionUpdateService.removeManagerFromSection(sectionId, manager.getUsername()));

		verify(sectionRepository, never()).save(any());
	}

	// ❌​ 404 - Not Found - User Not Found (Manager not in section)
	@Test
	void should_throw_when_user_is_not_manager_of_section() throws Exception {
		User managerInSection = User.builder()
				.username("manager1")
				.build();

		User managerToRemove = User.builder()
				.username("manager2")
				.build();

		Section section = Section.builder()
				.id("section-id")
				.name("Informática")
				.managers(new HashSet<>(Set.of(managerInSection)))
				.build();

		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername("manager2"))
				.thenReturn(managerToRemove);

		assertThrows(
				UserNotFoundException.class,
				() -> sectionUpdateService.removeManagerFromSection(sectionId, "manager2"));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== REMOVE COLLABORATOR ======================

	// ✅ 200 - OK
	@Test
	void should_remove_collaborator_from_section() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(collaborator.getUsername()))
				.thenReturn(collaborator);

		when(sectionRepository.save(any()))
				.thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.removeCollaboratorFromSection(
				sectionId,
				collaborator.getUsername());

		assertFalse(section.getCollaborators().contains(collaborator));
		assertEquals(section.getName(), response.getName());

		verify(sectionRepository).save(section);
	}

	// ❌ 404 - Not Found
	@Test
	void should_throw_when_section_not_found_remove_collaborator() {
		doThrow(new SectionNotFoundException("error"))
				.when(sectionService)
				.findSectionById(sectionId);

		assertThrows(
				SectionNotFoundException.class,
				() -> sectionUpdateService.removeCollaboratorFromSection(
						sectionId,
						collaborator.getUsername()));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found
	@Test
	void should_throw_when_collaborator_not_found_remove_collaborator() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(collaborator.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		assertThrows(
				UserNotFoundException.class,
				() -> sectionUpdateService.removeCollaboratorFromSection(
						sectionId,
						collaborator.getUsername()));

		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found (Collaborator not in section)
	@Test
	void should_throw_when_user_is_not_collaborator_of_section() throws Exception {
		User collaboratorToRemove = User.builder()
				.username("otherCollaborator")
				.build();

		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername("otherCollaborator"))
				.thenReturn(collaboratorToRemove);

		assertThrows(
				UserNotFoundException.class,
				() -> sectionUpdateService.removeCollaboratorFromSection(
						sectionId,
						"otherCollaborator"));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== ADD MANAGER ======================

	// ✅ 200 - OK
	@Test
	void should_add_manager_to_section() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(manager.getUsername()))
				.thenReturn(manager);

		when(sectionRepository.save(any()))
				.thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.addManagerToSection(
				sectionId,
				manager.getUsername());

		assertTrue(section.getManagers().contains(manager));
		assertEquals(section.getName(), response.getName());

		verify(managersMustBeEncargadosSpecification)
				.validate(Set.of(manager));
		verify(singleSectionManagerSpecification)
				.validate(Set.of(manager), sectionId);
		verify(sectionRepository).save(section);
	}

	// ❌ 404 - Section Not Found
	@Test
	void should_throw_when_section_not_found_add_manager() {
		doThrow(new SectionNotFoundException("error"))
				.when(sectionService)
				.findSectionById(sectionId);

		assertThrows(
				SectionNotFoundException.class,
				() -> sectionUpdateService.addManagerToSection(
						sectionId,
						manager.getUsername()));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found
	@Test
	void should_throw_when_manager_not_found_add_manager() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(manager.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		assertThrows(
				UserNotFoundException.class,
				() -> sectionUpdateService.addManagerToSection(
						sectionId,
						manager.getUsername()));

		verify(sectionRepository, never()).save(any());
	}

	// ❌ 400 - Invalid Manager Role
	@Test
	void should_throw_when_manager_has_invalid_role_add_manager() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(manager.getUsername()))
				.thenReturn(manager);

		doThrow(new InvalidManagerRoleException("error"))
				.when(managersMustBeEncargadosSpecification)
				.validate(Set.of(manager));

		assertThrows(
				InvalidManagerRoleException.class,
				() -> sectionUpdateService.addManagerToSection(
						sectionId,
						manager.getUsername()));

		verify(singleSectionManagerSpecification, never())
				.validate(anySet(), anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 409 - Manager Already Assigned
	@Test
	void should_throw_when_manager_already_assigned_to_other_section() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(manager.getUsername()))
				.thenReturn(manager);

		doThrow(new ManagerAlreadyAssignedException("error"))
				.when(singleSectionManagerSpecification)
				.validate(Set.of(manager), sectionId);

		assertThrows(
				ManagerAlreadyAssignedException.class,
				() -> sectionUpdateService.addManagerToSection(
						sectionId,
						manager.getUsername()));

		verify(sectionRepository, never()).save(any());
	}

	// ====================== ADD COLLABORATOR ======================

	// ✅ 200 - OK
	@Test
	void should_add_collaborator_to_section() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(collaborator.getUsername()))
				.thenReturn(collaborator);

		when(sectionRepository.save(any()))
				.thenAnswer(inv -> inv.getArgument(0));

		SectionResponse response = sectionUpdateService.addCollaboratorToSection(
				sectionId,
				collaborator.getUsername());

		assertTrue(section.getCollaborators().contains(collaborator));
		assertEquals(section.getName(), response.getName());

		verify(collaboratorsMustBeColaboradoresSpecification)
				.validate(Set.of(collaborator));
		verify(singleSectionCollaboratorSpecification)
				.validate(Set.of(collaborator), sectionId);
		verify(sectionRepository).save(section);
	}

	// ❌ 404 - Section Not Found
	@Test
	void should_throw_when_section_not_found_add_collaborator() {
		doThrow(new SectionNotFoundException("error"))
				.when(sectionService)
				.findSectionById(sectionId);

		assertThrows(
				SectionNotFoundException.class,
				() -> sectionUpdateService.addCollaboratorToSection(
						sectionId,
						collaborator.getUsername()));

		verify(userService, never()).findUserByUsername(anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 404 - User Not Found
	@Test
	void should_throw_when_collaborator_not_found_add_collaborator() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(collaborator.getUsername()))
				.thenThrow(new UserNotFoundException("error"));

		assertThrows(
				UserNotFoundException.class,
				() -> sectionUpdateService.addCollaboratorToSection(
						sectionId,
						collaborator.getUsername()));

		verify(sectionRepository, never()).save(any());
	}

	// ❌ 400 - Invalid Collaborator Role
	@Test
	void should_throw_when_collaborator_has_invalid_role_add_collaborator() {
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(collaborator.getUsername()))
				.thenReturn(collaborator);

		doThrow(new InvalidCollaboratorRoleException("error"))
				.when(collaboratorsMustBeColaboradoresSpecification)
				.validate(Set.of(collaborator));

		assertThrows(
				InvalidCollaboratorRoleException.class,
				() -> sectionUpdateService.addCollaboratorToSection(
						sectionId,
						collaborator.getUsername()));

		verify(singleSectionCollaboratorSpecification, never())
				.validate(anySet(), anyString());
		verify(sectionRepository, never()).save(any());
	}

	// ❌ 409 - Collaborator Already Assigned
	@Test
	void should_throw_when_collaborator_already_assigned_to_other_section() {
		String collaboratorUsername = collaborator.getUsername();
		when(sectionService.findSectionById(sectionId))
				.thenReturn(section);

		when(userService.findUserByUsername(collaborator.getUsername()))
				.thenReturn(collaborator);

		doThrow(new CollaboratorAlreadyAssignedException("error"))
				.when(singleSectionCollaboratorSpecification)
				.validate(Set.of(collaborator), sectionId);

		assertThrows(
				CollaboratorAlreadyAssignedException.class,
				() -> sectionUpdateService.addCollaboratorToSection(
						sectionId,
						collaboratorUsername));

		verify(sectionRepository, never()).save(any());
	}

}
