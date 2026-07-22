package com.tfg.cultura.api.sections.factory;

import java.util.List;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionCreateRequest;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

public class SectionFactory {

    public static Section validSection() {
        return Section.builder()
                .id("section_id")
                .name("Test Section")
                .build();
    }

    public static SectionCreateRequest validSectionCreateRequest() {
        User manager = UserFactory.validUser();
        manager.setRole(Role.ENCARGADO);

        User collaborator = UserFactory.validUser2();
        collaborator.setRole(Role.COLABORADOR);

        return SectionCreateRequest.builder()
                .name("Test Section")
                .managersUsernames(List.of(manager.getUsername()))
                .collaboratorsUsernames(List.of(collaborator.getUsername()))
                .build();
    }
    
}
