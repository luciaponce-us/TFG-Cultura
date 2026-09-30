package com.tfg.cultura.api.sections.service.specifications;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

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
public class ManagersMustBeEncargadosSpecification implements BusinessSpecification<Set<User>> {

	private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

	/**
	 * RN-07: Solo los usuarios que tienen el rol de encargado pueden ser nombrados
	 * gestores (managers) de una sección.
	 *
	 * @param managers
	 */
	@Override
	public void validate(Set<User> managers) throws ValidationException {
		List<String> nonEncargados = managers.stream().filter(manager -> manager.getRole() != Role.ENCARGADO)
				.map(User::getUsername).toList();

		if (!nonEncargados.isEmpty()) {
			throw new ValidationException(logger, Map.of("managers",
					"Los siguientes usuarios no son encargados: " + sanitize(nonEncargados.toString())));
		}
	}

}
