package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.sections.exception.CollaboratorAlreadyAssignedException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SingleSectionCollaboratorSpecificationTest {

	@Mock
	private SectionRepository sectionRepository;

	@InjectMocks
	private SingleSectionCollaboratorSpecification specification;

	@Test
	void should_not_throw_when_no_collaborator_is_assigned_to_another_section() {
		User collaborator1 = createUser("collaborator1");
		User collaborator2 = createUser("collaborator2");

		when(sectionRepository.findByCollaboratorsContaining(collaborator1)).thenReturn(Optional.empty());
		when(sectionRepository.findByCollaboratorsContaining(collaborator2)).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> specification.validate(Set.of(collaborator1, collaborator2)));

		verify(sectionRepository).findByCollaboratorsContaining(collaborator1);
		verify(sectionRepository).findByCollaboratorsContaining(collaborator2);
	}

	@Test
	void should_throw_when_a_collaborator_is_already_assigned_to_another_section() {
		User collaborator1 = createUser("collaborator1");
		User collaborator2 = createUser("collaborator2");

		when(sectionRepository.findByCollaboratorsContaining(collaborator1)).thenReturn(Optional.of(new Section()));
		when(sectionRepository.findByCollaboratorsContaining(collaborator2)).thenReturn(Optional.empty());

		Set<User> collaborators = Set.of(collaborator1, collaborator2);

		CollaboratorAlreadyAssignedException exception = assertThrows(CollaboratorAlreadyAssignedException.class,
				() -> specification.validate(collaborators));
		assertTrue(exception.getMessage().contains("collaborator1"));
	}

	@Test
	void should_throw_when_multiple_collaborators_are_already_assigned_to_other_sections() {
		User collaborator1 = createUser("collaborator1");
		User collaborator2 = createUser("collaborator2");

		when(sectionRepository.findByCollaboratorsContaining(collaborator1)).thenReturn(Optional.of(new Section()));
		when(sectionRepository.findByCollaboratorsContaining(collaborator2)).thenReturn(Optional.of(new Section()));

		Set<User> collaborators = Set.of(collaborator1, collaborator2);

		CollaboratorAlreadyAssignedException exception = assertThrows(CollaboratorAlreadyAssignedException.class,
				() -> specification.validate(collaborators));
		assertTrue(exception.getMessage().contains("collaborator1"));
		assertTrue(exception.getMessage().contains("collaborator2"));
	}

	private User createUser(String username) {
		return UserFactory.validUserWithUsernameAndRole(username, Role.COLABORADOR);
	}
}