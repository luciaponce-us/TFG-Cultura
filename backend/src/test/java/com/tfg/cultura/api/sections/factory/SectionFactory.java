package com.tfg.cultura.api.sections.factory;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import java.util.HashSet;
import java.util.Set;

public class SectionFactory {

	public static Section validSection() {
		User manager = UserFactory.validUser();
		manager.setRole(Role.ENCARGADO);

		User collaborator = UserFactory.validUser2();
		collaborator.setRole(Role.COLABORADOR);

		return Section.builder().id("section_id").name("Test Section").managers(new HashSet<>(Set.of(manager)))
				.collaborators(new HashSet<>(Set.of(collaborator))).build();
	}

	public static SectionCreateRequest validSectionCreateRequest(Section section) {
		String managerUsername = section.getManagers().stream().findFirst().get().getUsername();
		String collaboratorUsername = section.getCollaborators().stream().findFirst().get().getUsername();

		return SectionCreateRequest.builder().name(section.getName())
				.managersUsernames(new HashSet<>(Set.of(managerUsername)))
				.collaboratorsUsernames(new HashSet<>(Set.of(collaboratorUsername))).build();
	}

}
