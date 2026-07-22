package com.tfg.cultura.api.seeder.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SectionCsvRow {
    private String name;
    private List<String> managers;
    private List<String> collaborators;
}
