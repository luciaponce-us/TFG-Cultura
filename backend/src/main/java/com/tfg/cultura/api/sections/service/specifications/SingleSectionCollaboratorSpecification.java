package com.tfg.cultura.api.sections.service.specifications;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.CollaboratorAlreadyAssignedException;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SingleSectionCollaboratorSpecification implements BusinessSpecification<Set<User>> {

	private final SectionRepository sectionRepository;

	/**
	 * RN-10: Un usuario no puede estar nombrado como colaborador de más de una
	 * sección simultáneamente.
	 *
	 * @param collaborators
	 */
	@Override
	public void validate(Set<User> collaborators) {
		validate(collaborators, null);
	}

	public void validate(Set<User> collaborators, String currentSectionId) {
		List<String> alreadyAssignedCollaborators = new ArrayList<>();

		for (User collaborator : collaborators) {
			sectionRepository.findByCollaboratorsContaining(collaborator)
					.filter(section -> currentSectionId == null || !section.getId().equals(currentSectionId))
					.ifPresent(section -> alreadyAssignedCollaborators.add(collaborator.getUsername()));
		}

		if (!alreadyAssignedCollaborators.isEmpty()) {
			throw new CollaboratorAlreadyAssignedException(alreadyAssignedCollaborators.toString());
		}
	}

}
