package com.tfg.cultura.api.sections.service.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.ManagerAlreadyAssignedException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.model.User;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SingleSectionManagerSpecification implements BusinessSpecification<Set<User>> {

    private final SectionRepository sectionRepository;
    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");
    
    /**
     * RN-08: Un usuario no puede estar nombrado como gestor de más de una sección simultáneamente.
     * @param managers
     */
    @Override
    public void validate(Set<User> managers) throws ManagerAlreadyAssignedException {
        List<String> alreadyAssignedManagers = new ArrayList<>();

        for (User manager : managers) {
            Optional<Section> sectionWithManager = sectionRepository.findByManagersContaining(manager);
            if (sectionWithManager.isPresent()) {
                alreadyAssignedManagers.add(manager.getUsername());
            }
        }

        if (!alreadyAssignedManagers.isEmpty()) {
            logger.error("Los siguientes usuarios ya están asignados como gestores: {}", alreadyAssignedManagers);
            throw new ManagerAlreadyAssignedException(
                    "Los siguientes usuarios ya están asignados como gestores: " + alreadyAssignedManagers);
        }
    }

    public void validateForUpdate(Set<User> managers, String currentSectionId) throws ManagerAlreadyAssignedException {
        List<String> alreadyAssignedManagers = new ArrayList<>();

        for (User manager : managers) {
            Optional<Section> sectionWithManager = sectionRepository.findByManagersContaining(manager);
            if (sectionWithManager.isPresent() && !sectionWithManager.get().getId().equals(currentSectionId)) {
                alreadyAssignedManagers.add(manager.getUsername());
            }
        }

        if (!alreadyAssignedManagers.isEmpty()) {
            logger.error("Los siguientes usuarios ya están asignados como gestores en otra sección: {}", alreadyAssignedManagers);
            throw new ManagerAlreadyAssignedException(
                    "Los siguientes usuarios ya están asignados como gestores en otra sección: " + alreadyAssignedManagers);
        }
    }
    
}
