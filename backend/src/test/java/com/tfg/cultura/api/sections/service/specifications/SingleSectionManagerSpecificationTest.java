package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.sections.exception.ManagerAlreadyAssignedException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

@ExtendWith(MockitoExtension.class)
class SingleSectionManagerSpecificationTest {

    @Mock
    private SectionRepository sectionRepository;

    private SingleSectionManagerSpecification specification;

    @BeforeEach
    void setUp() {
        specification = new SingleSectionManagerSpecification(sectionRepository);
    }

    @Test
    void should_not_throw_when_no_manager_is_assigned_to_another_section() {
        User manager1 = createUser("manager1");
        User manager2 = createUser("manager2");

        when(sectionRepository.findByManagersContaining(manager1))
                .thenReturn(Optional.empty());
        when(sectionRepository.findByManagersContaining(manager2))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() ->
                specification.validate(Set.of(manager1, manager2)));

        verify(sectionRepository).findByManagersContaining(manager1);
        verify(sectionRepository).findByManagersContaining(manager2);
    }

    @Test
    void should_throw_when_a_manager_is_already_assigned_to_another_section() {
        User manager1 = createUser("manager1");
        User manager2 = createUser("manager2");

        when(sectionRepository.findByManagersContaining(manager1))
                .thenReturn(Optional.of(new Section()));
        when(sectionRepository.findByManagersContaining(manager2))
                .thenReturn(Optional.empty());

        ManagerAlreadyAssignedException exception = assertThrows(
                ManagerAlreadyAssignedException.class,
                () -> specification.validate(Set.of(manager1, manager2)));

        assertTrue(exception.getMessage().contains("manager1"));
    }

    @Test
    void should_throw_when_multiple_managers_are_already_assigned_to_other_sections() {
        User manager1 = createUser("manager1");
        User manager2 = createUser("manager2");

        when(sectionRepository.findByManagersContaining(manager1))
                .thenReturn(Optional.of(new Section()));
        when(sectionRepository.findByManagersContaining(manager2))
                .thenReturn(Optional.of(new Section()));

        ManagerAlreadyAssignedException exception = assertThrows(
                ManagerAlreadyAssignedException.class,
                () -> specification.validate(Set.of(manager1, manager2)));

        assertTrue(exception.getMessage().contains("manager1"));
        assertTrue(exception.getMessage().contains("manager2"));
    }

    private User createUser(String username) {
        return UserFactory.validUserWithUsernameAndRole(username, Role.ENCARGADO);
    }
}