package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.sections.exception.InvalidCollaboratorRoleException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

@ExtendWith(MockitoExtension.class)
class CollaboratorsMustBeColaboradoresSpecificationTest {

    @InjectMocks
    private CollaboratorsMustBeColaboradoresSpecification specification;

    @Test
    void should_not_throw_when_all_users_are_colaboradores() {
        Set<User> collaborators = Set.of(createUser("user1", Role.COLABORADOR),
                createUser("user2", Role.COLABORADOR));

        assertDoesNotThrow(() -> specification.validate(collaborators));
    }

    @Test
    void should_throw_when_a_user_is_not_colaborador() {
        Set<User> collaborators = Set.of(
                createUser("user1", Role.COLABORADOR),
                createUser("manager", Role.ENCARGADO));

        InvalidCollaboratorRoleException exception = assertThrows(
                InvalidCollaboratorRoleException.class,
                () -> specification.validate(collaborators));

        assertTrue(exception.getMessage().contains("manager"));
    }

    @Test
    void should_throw_when_multiple_users_are_not_colaboradores() {
        Set<User> collaborators = Set.of(
                createUser("user1", Role.COLABORADOR),
                createUser("manager", Role.ENCARGADO),
                createUser("admin", Role.COORDINADOR));

        InvalidCollaboratorRoleException exception = assertThrows(
                InvalidCollaboratorRoleException.class,
                () -> specification.validate(collaborators));

        assertTrue(exception.getMessage().contains("manager"));
        assertTrue(exception.getMessage().contains("admin"));
    }

    private User createUser(String username, Role role) {
        return UserFactory.validUserWithUsernameAndRole(username, role);
    }
}
