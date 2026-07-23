package com.tfg.cultura.api.sections.service.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.CollaboratorAlreadyAssignedException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.model.User;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SingleSectionCollaboratorSpecification implements BusinessSpecification<Set<User>> {

    private final SectionRepository sectionRepository;
    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");
    
    /**
     * RN-10: Un usuario no puede estar nombrado como colaborador de más de una sección simultáneamente.
     * @param collaborators
     */
    @Override
    public void validate(Set<User> collaborators) throws CollaboratorAlreadyAssignedException {
        List<String> alreadyAssignedCollaborators = new ArrayList<>();

        for (User collaborator : collaborators) {
            Optional<Section> sectionWithCollaborator = sectionRepository.findByCollaboratorsContaining(collaborator);
            if (sectionWithCollaborator.isPresent()) {
                alreadyAssignedCollaborators.add(collaborator.getUsername());
            }
        }

        if (!alreadyAssignedCollaborators.isEmpty()) {
            logger.error("Los siguientes usuarios ya están asignados como colaboradores de otras secciones: {}", alreadyAssignedCollaborators);
            throw new CollaboratorAlreadyAssignedException(
                    "Los siguientes usuarios ya están asignados como colaboradores de otras secciones: " + alreadyAssignedCollaborators);
        }
    }

    public void validateForUpdate(Set<User> collaboratorsSet, String id) {
        List<String> alreadyAssignedCollaborators = new ArrayList<>();

        for (User collaborator : collaboratorsSet) {
            Optional<Section> sectionWithCollaborator = sectionRepository.findByCollaboratorsContaining(collaborator);
            if (sectionWithCollaborator.isPresent() && !sectionWithCollaborator.get().getId().equals(id)) {
                alreadyAssignedCollaborators.add(collaborator.getUsername());
            }
        }

        if (!alreadyAssignedCollaborators.isEmpty()) {
            logger.error("Los siguientes usuarios ya están asignados como colaboradores de otras secciones: {}", alreadyAssignedCollaborators);
            throw new CollaboratorAlreadyAssignedException(
                    "Los siguientes usuarios ya están asignados como colaboradores de otras secciones: " + alreadyAssignedCollaborators);
        }
    }
    
}
