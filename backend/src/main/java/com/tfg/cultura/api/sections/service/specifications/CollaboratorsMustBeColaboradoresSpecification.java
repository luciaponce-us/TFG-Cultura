package com.tfg.cultura.api.sections.service.specifications;

import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CollaboratorsMustBeColaboradoresSpecification implements BusinessSpecification<Set<User>> {

	private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

	/**
	 * RN-09: Solo los usuarios que tienen el rol de colaborador pueden ser
	 * nombrados colaboradores (collaborators) de una sección.
	 *
	 * @param collaborators
	 */
	@Override
	public void validate(Set<User> collaborators) throws ValidationException {
		List<String> nonColaboradores = collaborators.stream()
				.filter(collaborator -> collaborator.getRole() != Role.COLABORADOR).map(User::getUsername).toList();

		if (!nonColaboradores.isEmpty()) {
			throw new ValidationException(logger, Map.of("collaborators",
					"Los siguientes usuarios no tienen el rol de colaborador: " + nonColaboradores));
		}
	}

}
