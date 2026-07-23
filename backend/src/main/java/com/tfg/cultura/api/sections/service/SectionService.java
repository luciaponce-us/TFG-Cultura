package com.tfg.cultura.api.sections.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.core.utils.LoggerSanitizer;
import com.tfg.cultura.api.sections.exception.*;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.sections.service.specifications.*;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final UserService userService;

    // SPECIFICATIONS - BUSINESS RULES
    private final UniqueSectionNameSpecification uniqueSectionNameSpecification;
    private final ManagersMustBeEncargadosSpecification managersMustBeEncargadosSpecification;
    private final SingleSectionManagerSpecification singleSectionManagerSpecification;
    private final CollaboratorsMustBeColaboradoresSpecification collaboratorsMustBeColaboradoresSpecification;
    private final SingleSectionCollaboratorSpecification singleSectionCollaboratorSpecification;

    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

    // HELPER

    Section findSectionById(String id) throws SectionNotFoundException {
        return sectionRepository.findById(id)
                .orElseThrow(() -> {
                    String sanitizedId = LoggerSanitizer.sanitize(id);
                    logger.error("Sección no encontrada con ID: {}", sanitizedId);
                    return new SectionNotFoundException("Sección no encontrada con ID: " + sanitizedId);
                });
    }

    void setSectionManagers(Section section, Set<String> managersUsernames)
            throws InvalidManagerRoleException, ManagerAlreadyAssignedException {

        Set<User> managers = userService.findUsersByUsernames(managersUsernames);

        managersMustBeEncargadosSpecification.validate(managers);
        singleSectionManagerSpecification.validate(managers, section.getId());

        section.setManagers(managers);
    }

    void setSectionCollaborators(Section section, Set<String> collaboratorsUsernames)
            throws InvalidCollaboratorRoleException, CollaboratorAlreadyAssignedException {

        Set<User> collaborators = userService.findUsersByUsernames(collaboratorsUsernames);

        collaboratorsMustBeColaboradoresSpecification.validate(collaborators);
        singleSectionCollaboratorSpecification.validate(collaborators, section.getId());

        section.setCollaborators(collaborators);
    }

    // CREATE

    public SectionResponse createSection(SectionCreateRequest request) throws SectionAlreadyExistsException,
            InvalidManagerRoleException, ManagerAlreadyAssignedException,
            InvalidCollaboratorRoleException, CollaboratorAlreadyAssignedException {

        uniqueSectionNameSpecification.validate(request.getName());

        Section section = Section.builder()
                .name(request.getName())
                .build();

        setSectionManagers(section, request.getManagersUsernames());
        setSectionCollaborators(section, request.getCollaboratorsUsernames());

        Section savedSection = sectionRepository.save(section);
        logger.info("Sección creada con éxito: {}", savedSection.getName());

        return new SectionResponse(savedSection);
    }

    // GET

    public List<SectionResponse> getAllSections(String nameFilter) {
        List<Section> sections;
        if (nameFilter == null || nameFilter.isEmpty()) {
            sections = sectionRepository.findAll();
        } else {
            sections = sectionRepository.findAllByNameContainingIgnoreCase(nameFilter);
        }
        return sections.stream().map(SectionResponse::new).toList();
    }

    public SectionResponse getSectionById(String id) throws SectionNotFoundException {
        Section section = findSectionById(id);
        return new SectionResponse(section);
    }

    // UPDATE

    public SectionResponse updateSection(String id, SectionCreateRequest request)
            throws SectionNotFoundException, SectionAlreadyExistsException {
        Section section = findSectionById(id);
        uniqueSectionNameSpecification.validateForUpdate(request.getName(), id);

        section.setName(request.getName());
        setSectionManagers(section, request.getManagersUsernames());
        setSectionCollaborators(section, request.getCollaboratorsUsernames());

        Section updatedSection = sectionRepository.save(section);
        logger.info("Sección actualizada con éxito: {}", updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

    public SectionResponse removeManagerFromSection(String sectionId, String managerUsername)
            throws SectionNotFoundException, UserNotFoundException {
        Section section = findSectionById(sectionId);
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
        Section section = findSectionById(sectionId);
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
        Section section = findSectionById(sectionId);
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
        Section section = findSectionById(sectionId);
        User collaborator = userService.findUserByUsername(collaboratorUsername);

        collaboratorsMustBeColaboradoresSpecification.validate(Set.of(collaborator));
        singleSectionCollaboratorSpecification.validate(Set.of(collaborator), section.getId());

        section.getCollaborators().add(collaborator);
        Section updatedSection = sectionRepository.save(section);
        logger.info("Colaborador '{}' añadido a la sección '{}'", collaboratorUsername, updatedSection.getName());

        return new SectionResponse(updatedSection);
    }

}
