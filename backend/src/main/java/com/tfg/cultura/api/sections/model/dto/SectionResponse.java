package com.tfg.cultura.api.sections.model.dto;

import java.util.List;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.users.model.dto.UserResponse;

import lombok.Getter;

@Getter
public class SectionResponse {
    private String id;
    private String name;
    private List<UserResponse> managers;
    private List<UserResponse> collaborators;

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.name = section.getName();
        this.managers = section.getManagers().stream().map(UserResponse::new).toList();
        this.collaborators = section.getCollaborators().stream().map(UserResponse::new).toList();
    }
}
