package com.tfg.cultura.api.sections.factory;

import java.util.List;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

public class SectionFactory {

    public static Section validSection() {
        User manager = UserFactory.validUser();
        manager.setRole(Role.ENCARGADO);

        User collaborator = UserFactory.validUser2();
        collaborator.setRole(Role.COLABORADOR);

        return Section.builder()
                .id("section_id")
                .name("Test Section")
                .managers(List.of(manager))
                .collaborators(List.of(collaborator))
                .build();
    }

    public static SectionCreateRequest validSectionCreateRequest(Section section) {
        String managerUsername = section.getManagers().getFirst().getUsername();
        String collaboratorUsername = section.getCollaborators().getFirst().getUsername();

        return SectionCreateRequest.builder()
                .name(section.getName())
                .managersUsernames(List.of(managerUsername))
                .collaboratorsUsernames(List.of(collaboratorUsername))
                .build();
    }
    
}
