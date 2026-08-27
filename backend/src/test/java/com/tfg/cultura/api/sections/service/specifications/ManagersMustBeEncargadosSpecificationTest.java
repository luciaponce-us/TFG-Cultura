package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfg.cultura.api.sections.exception.InvalidManagerRoleException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManagersMustBeEncargadosSpecificationTest {

	@InjectMocks
	private ManagersMustBeEncargadosSpecification specification;

	@Test
	void should_not_throw_when_all_users_are_encargados() {
		Set<User> managers = Set.of(createUser("manager1", Role.ENCARGADO), createUser("manager2", Role.ENCARGADO));

		assertDoesNotThrow(() -> specification.validate(managers));
	}

	@Test
	void should_throw_when_a_user_is_not_encargado() {
		Set<User> managers = Set.of(createUser("manager1", Role.ENCARGADO),
				createUser("collaborator", Role.COLABORADOR));

		InvalidManagerRoleException exception = assertThrows(InvalidManagerRoleException.class,
				() -> specification.validate(managers));

		assertTrue(exception.getMessage().contains("collaborator"));
	}

	@Test
	void should_throw_when_multiple_users_are_not_encargados() {
		Set<User> managers = Set.of(createUser("manager1", Role.ENCARGADO),
				createUser("collaborator", Role.COLABORADOR), createUser("coordinator", Role.COORDINADOR));

		InvalidManagerRoleException exception = assertThrows(InvalidManagerRoleException.class,
				() -> specification.validate(managers));

		assertTrue(exception.getMessage().contains("collaborator"));
		assertTrue(exception.getMessage().contains("coordinator"));
	}

	private User createUser(String username, Role role) {
		return UserFactory.validUserWithUsernameAndRole(username, role);
	}
}
