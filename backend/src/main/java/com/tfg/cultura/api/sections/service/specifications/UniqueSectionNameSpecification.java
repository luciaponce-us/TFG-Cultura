package com.tfg.cultura.api.sections.service.specifications;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.SectionAlreadyExistsException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class UniqueSectionNameSpecification implements BusinessSpecification<String> {
    private final SectionRepository sectionRepository;
    private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

    @Override
    public void validate(String name) throws SectionAlreadyExistsException {
        Optional<Section> existingSection = sectionRepository.findByName(name);
        if (existingSection.isPresent()) {
            String existingSectionName = existingSection.get().getName();
            logger.error("La sección con nombre '{}' ya existe", existingSectionName);
            throw new SectionAlreadyExistsException("La sección con nombre '" + existingSectionName + "' ya existe");
        }
    }
    
}
