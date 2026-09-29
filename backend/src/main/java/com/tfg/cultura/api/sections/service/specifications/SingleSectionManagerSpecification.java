package com.tfg.cultura.api.sections.service.specifications;

import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import com.tfg.cultura.api.users.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SingleSectionManagerSpecification implements BusinessSpecification<Set<User>> {

	private final SectionRepository sectionRepository;

	private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

	/**
	 * RN-08: Un usuario no puede estar nombrado como gestor de más de una sección
	 * simultáneamente.
	 *
	 * @param managers
	 */
	@Override
	public void validate(Set<User> managers) throws FieldException {
		validate(managers, null);
	}

	public void validate(Set<User> managers, String currentSectionId) throws FieldException {
		List<String> alreadyAssignedManagers = new ArrayList<>();

		for (User manager : managers) {
			sectionRepository.findByManagersContaining(manager)
					.filter(section -> currentSectionId == null || !section.getId().equals(currentSectionId))
					.ifPresent(section -> alreadyAssignedManagers.add(manager.getUsername()));
		}

		if (!alreadyAssignedManagers.isEmpty()) {
			throw new FieldException(logger,HttpStatus.CONFLICT,Map.of("managers", "El gestor ya está asignado a otra sección: " + alreadyAssignedManagers.toString()));
		}
	}

}
