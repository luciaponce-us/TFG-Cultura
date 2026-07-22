package com.tfg.cultura.api.sections.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.sections.exception.SectionAlreadyExistsException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

    // HELPER

    void checkIfSectionExists(String name) {
        Optional<Section> existingSection = sectionRepository.findByName(name);
        if (existingSection.isPresent()) {
            String existingSectionName = existingSection.get().getName();
            logger.warn("La sección con nombre '{}' ya existe", existingSectionName);
            throw new SectionAlreadyExistsException("La sección con nombre '" + existingSectionName + "' ya existe");
        }
    }

    Map<String, User> getUsersByUsernames(Collection<String> usernames) {
        List<User> users = userRepository.findByUsernameIn(usernames);

        Map<String, User> usersByUsername = users.stream()
                .collect(Collectors.toMap(User::getUsername, Function.identity()));

        List<String> missingUsernames = usernames.stream()
                .filter(username -> !usersByUsername.containsKey(username))
                .toList();

        if (!missingUsernames.isEmpty()) {
            logger.warn("Los siguientes usuarios no existen: {}", missingUsernames);
            throw new UserNotFoundException(
                    "Los siguientes usuarios no existen: " + missingUsernames);
        }

        return usersByUsername;
    }

    // CREATE

    public SectionResponse createSection(SectionCreateRequest request) {
        checkIfSectionExists(request.getName());

        Set<String> usernames = Stream.concat(
                request.getManagersUsernames().stream(),
                request.getCollaboratorsUsernames().stream())
                .collect(Collectors.toSet());

        Map<String, User> usersByUsername = getUsersByUsernames(usernames);

        List<User> managers = request.getManagersUsernames().stream()
                .map(usersByUsername::get)
                .toList();

        List<User> collaborators = request.getCollaboratorsUsernames().stream()
                .map(usersByUsername::get)
                .toList();

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
