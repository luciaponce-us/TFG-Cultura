package com.tfg.cultura.api.sections.service.specifications;

import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;

import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UniqueSectionNameSpecification implements BusinessSpecification<String> {
	private final SectionRepository sectionRepository;
	private static final Logger logger = LoggerFactory.getLogger("sectionsLogger");

	@Override
	public void validate(String name) throws DuplicationException {
		Optional<Section> existingSection = sectionRepository.findByName(name);
		if (existingSection.isPresent()) {
			String existingSectionName = existingSection.get().getName();
			throw new DuplicationException(logger,
					Map.of("name", "La sección con nombre " + existingSectionName + " ya existe."));
		}
	}

	public void validateForUpdate(String name, String currentSectionId) throws DuplicationException {
		Optional<Section> existingSection = sectionRepository.findByName(name);
		if (existingSection.isPresent() && !existingSection.get().getId().equals(currentSectionId)) {
			String existingSectionName = existingSection.get().getName();
			throw new DuplicationException(logger,
					Map.of("name", "La sección con nombre " + existingSectionName + " ya existe."));
		}
	}

}
