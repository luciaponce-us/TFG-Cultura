package com.tfg.cultura.api.sections.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.sections.exception.*;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.sections.service.specifications.*;
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

    // CREATE

    public SectionResponse createSection(SectionCreateRequest request) throws SectionAlreadyExistsException,
            InvalidManagerRoleException, ManagerAlreadyAssignedException,
            InvalidCollaboratorRoleException, CollaboratorAlreadyAssignedException {

        uniqueSectionNameSpecification.validate(request.getName());

        Set<String> usernames = Stream.concat(
                request.getManagersUsernames().stream(),
                request.getCollaboratorsUsernames().stream())
                .collect(Collectors.toSet());

        Map<String, User> usersByUsername = userService.getUsersByUsernames(usernames);

        List<User> managers = request.getManagersUsernames().stream()
                .map(usersByUsername::get)
                .toList();
        Set<User> managersSet = managers.stream().collect(Collectors.toSet());

        managersMustBeEncargadosSpecification.validate(managersSet);
        singleSectionManagerSpecification.validate(managersSet);

        List<User> collaborators = request.getCollaboratorsUsernames().stream()
                .map(usersByUsername::get)
                .toList();

        Set<User> collaboratorsSet = collaborators.stream().collect(Collectors.toSet());
        collaboratorsMustBeColaboradoresSpecification.validate(collaboratorsSet);
        singleSectionCollaboratorSpecification.validate(collaboratorsSet);

        Section section = Section.builder()
                .name(request.getName())
                .managers(managers)
                .collaborators(collaborators)
                .build();

        Section savedSection = sectionRepository.save(section);
        logger.info("Sección creada con éxito: {}", savedSection.getName());

        return new SectionResponse(savedSection);
    }

}
