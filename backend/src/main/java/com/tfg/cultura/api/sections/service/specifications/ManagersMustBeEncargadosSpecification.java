package com.tfg.cultura.api.sections.service.specifications;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.InvalidManagerRoleException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ManagersMustBeEncargadosSpecification implements BusinessSpecification<Set<User>> {

	/**
	 * RN-07: Solo los usuarios que tienen el rol de encargado pueden ser nombrados
	 * gestores (managers) de una sección.
	 *
	 * @param managers
	 */
	@Override
	public void validate(Set<User> managers) throws InvalidManagerRoleException {
		List<String> nonEncargados = managers.stream().filter(manager -> manager.getRole() != Role.ENCARGADO)
				.map(User::getUsername).toList();

		if (!nonEncargados.isEmpty()) {
			throw new InvalidManagerRoleException(nonEncargados.toString());
		}
	}

}
