package com.tfg.cultura.api.sections.service.specifications;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.InvalidCollaboratorRoleException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

@Component
public class CollaboratorsMustBeColaboradoresSpecification implements BusinessSpecification<Set<User>> {
    
    /**
     * RN-09: Solo los usuarios que tienen el rol de colaborador pueden ser nombrados colaboradores (collaborators) de una sección.
     * @param collaborators
     */
    @Override
    public void validate(Set<User> collaborators) throws InvalidCollaboratorRoleException {
        List<String> nonColaboradores = collaborators.stream()
                .filter(collaborator -> collaborator.getRole() != Role.COLABORADOR)
                .map(User::getUsername)
                .toList();

        if (!nonColaboradores.isEmpty()) {
            throw new InvalidCollaboratorRoleException(nonColaboradores.toString());
        }
    }
    
}
