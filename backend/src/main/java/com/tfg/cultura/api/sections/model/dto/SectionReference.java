package com.tfg.cultura.api.sections.model.dto;

import com.tfg.cultura.api.sections.model.Section;

import lombok.Getter;

@Getter
public class SectionReference {
    private String id;
    private String name;

    public SectionReference(Section section) {
        this.id = section.getId();
        this.name = section.getName();
    }
}
