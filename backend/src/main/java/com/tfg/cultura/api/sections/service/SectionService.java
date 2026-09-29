package com.tfg.cultura.api.sections.service;

import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.sections.model.dto.SectionResponse;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.sections.service.specifications.*;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

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

	public Section findSectionById(String id) throws NotFoundException {
		return sectionRepository.findById(id).orElseThrow(() -> {
			return new NotFoundException("Sección no encontrada con ID: " + sanitize(id), logger);
		});
	}

	void setSectionManagers(Section section, Set<String> managersUsernames) throws ValidationException, FieldException {

		Set<User> managers = userService.findUsersByUsernames(managersUsernames);

		managersMustBeEncargadosSpecification.validate(managers);
		singleSectionManagerSpecification.validate(managers, section.getId());

		section.setManagers(managers);
	}

	void setSectionCollaborators(Section section, Set<String> collaboratorsUsernames)
			throws ValidationException, FieldException {

		Set<User> collaborators = userService.findUsersByUsernames(collaboratorsUsernames);

		collaboratorsMustBeColaboradoresSpecification.validate(collaborators);
		singleSectionCollaboratorSpecification.validate(collaborators, section.getId());

		section.setCollaborators(collaborators);
	}

	// CREATE

	public SectionResponse createSection(SectionCreateRequest request) throws DuplicationException, ValidationException, FieldException {

		uniqueSectionNameSpecification.validate(request.getName());

		Section section = Section.builder().name(request.getName()).build();

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

	public SectionResponse getSectionById(String id) throws NotFoundException {
		Section section = findSectionById(id);
		return new SectionResponse(section);
	}

	// DELETE

	public void deleteSection(String id) throws NotFoundException {
		Section section = findSectionById(id);
		sectionRepository.delete(section);
		logger.info("Sección eliminada con éxito: {}", section.getName());
	}

}
