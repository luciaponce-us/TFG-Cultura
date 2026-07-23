package com.tfg.cultura.api.sections.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.sections.exception.*;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.sections.service.specifications.*;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SectionUpdateService {
    private final SectionService sectionService;
    private final SectionRepository sectionRepository;
    private final UserService userService;

    // SPECIFICATIONS - BUSINESS RULES
    private final UniqueSectionNameSpecification uniqueSectionNameSpecification;
    private final ManagersMustBeEncargadosSpecification managersMustBeEncargadosSpecification;
    private final SingleSectionManagerSpecification singleSectionManagerSpecification;
    private final CollaboratorsMustBeColaboradoresSpecification collaboratorsMustBeColaboradoresSpecification;
    private final SingleSectionCollaboratorSpecification singleSectionCollaboratorSpecification;

    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

    public SectionResponse updateSection(String id, SectionCreateRequest request)
            throws SectionNotFoundException, SectionAlreadyExistsException {
        Section section = sectionService.findSectionById(id);
        uniqueSectionNameSpecification.validateForUpdate(request.getName(), id);

        section.setName(request.getName());
        sectionService.setSectionManagers(section, request.getManagersUsernames());
        sectionService.setSectionCollaborators(section, request.getCollaboratorsUsernames());

        Section updatedSection = sectionRepository.save(section);
        logger.info("Sección actualizada con éxito: {}", updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

    public SectionResponse removeManagerFromSection(String sectionId, String managerUsername)
            throws SectionNotFoundException, UserNotFoundException {
        Section section = sectionService.findSectionById(sectionId);
        User manager = userService.findUserByUsername(managerUsername);

        Set<String> managerUsernames = section.getManagers().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        if (!managerUsernames.contains(manager.getUsername())) {
            logger.error("El usuario '{}' no es un encargado de la sección '{}'. Encargados actuales: {}",
                    managerUsername, section.getName(), managerUsernames);
            throw new UserNotFoundException("El usuario '" + managerUsername + "' no es un encargado de la sección '"
                    + section.getName() + "'. Encargados actuales: " + managerUsernames);
        }

        User foundManager = section.getManagers().stream()
                .filter(m -> m.getUsername().equals(managerUsername))
                .findFirst().get();
        section.getManagers().remove(foundManager);
        Section updatedSection = sectionRepository.save(section);
        logger.info("Encargado '{}' eliminado de la sección '{}'", managerUsername, updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

    public SectionResponse removeCollaboratorFromSection(String sectionId, String collaboratorUsername)
            throws SectionNotFoundException, UserNotFoundException {
        Section section = sectionService.findSectionById(sectionId);
        User collaborator = userService.findUserByUsername(collaboratorUsername);

        Set<String> collaboratorUsernames = section.getCollaborators().stream()
                .map(User::getUsername)
                .collect(Collectors.toSet());
        if (!collaboratorUsernames.contains(collaborator.getUsername())) {
            logger.error("El usuario '{}' no es un colaborador de la sección '{}'. Colaboradores actuales: {}",
                    collaboratorUsername, section.getName(), collaboratorUsernames);
            throw new UserNotFoundException(
                    "El usuario '" + collaboratorUsername + "' no es un colaborador de la sección '" + section.getName()
                            + "'. Colaboradores actuales: " + collaboratorUsernames);
        }

        User foundCollaborator = section.getCollaborators().stream()
                .filter(c -> c.getUsername().equals(collaboratorUsername))
                .findFirst().get();
        section.getCollaborators().remove(foundCollaborator);
        Section updatedSection = sectionRepository.save(section);
        logger.info("Colaborador '{}' eliminado de la sección '{}'", collaboratorUsername, updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

    public SectionResponse addManagerToSection(String sectionId, String managerUsername)
            throws SectionNotFoundException, UserNotFoundException, InvalidManagerRoleException,
            ManagerAlreadyAssignedException {
        Section section = sectionService.findSectionById(sectionId);
        User manager = userService.findUserByUsername(managerUsername);

        managersMustBeEncargadosSpecification.validate(Set.of(manager));
        singleSectionManagerSpecification.validate(Set.of(manager), section.getId());

        section.getManagers().add(manager);
        Section updatedSection = sectionRepository.save(section);
        logger.info("Encargado '{}' añadido a la sección '{}'", managerUsername, updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

    public SectionResponse addCollaboratorToSection(String sectionId, String collaboratorUsername)
            throws SectionNotFoundException, UserNotFoundException, InvalidCollaboratorRoleException,
            CollaboratorAlreadyAssignedException {
        Section section = sectionService.findSectionById(sectionId);
        User collaborator = userService.findUserByUsername(collaboratorUsername);

        collaboratorsMustBeColaboradoresSpecification.validate(Set.of(collaborator));
        singleSectionCollaboratorSpecification.validate(Set.of(collaborator), section.getId());

        section.getCollaborators().add(collaborator);
        Section updatedSection = sectionRepository.save(section);
        logger.info("Colaborador '{}' añadido a la sección '{}'", collaboratorUsername, updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

}
