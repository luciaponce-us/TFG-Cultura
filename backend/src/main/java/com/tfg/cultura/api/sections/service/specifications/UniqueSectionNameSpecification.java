package com.tfg.cultura.api.sections.service.specifications;

import com.tfg.cultura.api.core.service.BusinessSpecification;
import com.tfg.cultura.api.sections.exception.SectionAlreadyExistsException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UniqueSectionNameSpecification implements BusinessSpecification<String> {
	private final SectionRepository sectionRepository;

	@Override
	public void validate(String name) throws SectionAlreadyExistsException {
		Optional<Section> existingSection = sectionRepository.findByName(name);
		if (existingSection.isPresent()) {
			String existingSectionName = existingSection.get().getName();
			throw new SectionAlreadyExistsException(existingSectionName);
		}
	}

	public void validateForUpdate(String name, String currentSectionId) throws SectionAlreadyExistsException {
		Optional<Section> existingSection = sectionRepository.findByName(name);
		if (existingSection.isPresent() && !existingSection.get().getId().equals(currentSectionId)) {
			String existingSectionName = existingSection.get().getName();
			throw new SectionAlreadyExistsException(existingSectionName);
		}
	}

}
