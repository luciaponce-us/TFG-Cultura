package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CollaboratorsMustBeColaboradoresSpecificationTest {

	@InjectMocks
	private CollaboratorsMustBeColaboradoresSpecification specification;

	@Test
	void should_not_throw_when_all_users_are_colaboradores() {
		Set<User> collaborators = Set.of(createUser("user1", Role.COLABORADOR), createUser("user2", Role.COLABORADOR));

		assertDoesNotThrow(() -> specification.validate(collaborators));
	}

	@Test
	void should_throw_when_a_user_is_not_colaborador() {
		Set<User> collaborators = Set.of(createUser("user1", Role.COLABORADOR), createUser("manager", Role.ENCARGADO));

		ValidationException exception = assertThrows(ValidationException.class,
				() -> specification.validate(collaborators));

		assertNotNull(exception.getErrors().get("collaboratorsUsernames"));
	}

	@Test
	void should_throw_when_multiple_users_are_not_colaboradores() {
		Set<User> collaborators = Set.of(createUser("user1", Role.COLABORADOR), createUser("manager", Role.ENCARGADO),
				createUser("admin", Role.COORDINADOR));

		ValidationException exception = assertThrows(ValidationException.class,
				() -> specification.validate(collaborators));

		assertNotNull(exception.getErrors().get("collaboratorsUsernames"));
	}

	private User createUser(String username, Role role) {
		return UserFactory.validUserWithUsernameAndRole(username, role);
	}
}
