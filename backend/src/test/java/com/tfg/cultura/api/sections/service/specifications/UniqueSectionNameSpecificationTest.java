package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.sections.exception.SectionAlreadyExistsException;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UniqueSectionNameSpecificationTest {

	@Mock
	private SectionRepository sectionRepository;

	@InjectMocks
	private UniqueSectionNameSpecification specification;

	@Test
	void should_not_throw_when_section_name_does_not_exist() {
		when(sectionRepository.findByName("Section")).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> specification.validate("Section"));

		verify(sectionRepository).findByName("Section");
	}

	@Test
	void should_throw_when_section_name_already_exists() {
		Section section = new Section();
		section.setName("Section");

		when(sectionRepository.findByName("Section")).thenReturn(Optional.of(section));

		SectionAlreadyExistsException exception = assertThrows(SectionAlreadyExistsException.class,
				() -> specification.validate("Section"));

		assertTrue(exception.getMessage().contains("Section"));

		verify(sectionRepository).findByName("Section");
	}

	@Test
	void should_not_throw_when_section_name_does_not_exist_on_update() {
		when(sectionRepository.findByName("Nueva sección")).thenReturn(Optional.empty());

		assertDoesNotThrow(() -> specification.validateForUpdate("Nueva sección", "sectionId"));

		verify(sectionRepository).findByName("Nueva sección");
	}

	@Test
	void should_not_throw_when_section_name_belongs_to_current_section() {
		Section section = SectionFactory.validSection();
		section.setId("sectionId");
		section.setName("Cultura");

		when(sectionRepository.findByName("Cultura")).thenReturn(Optional.of(section));

		assertDoesNotThrow(() -> specification.validateForUpdate("Cultura", "sectionId"));

		verify(sectionRepository).findByName("Cultura");
	}

	@Test
	void should_throw_when_section_name_belongs_to_another_section() {
		Section existingSection = SectionFactory.validSection();
		existingSection.setId("otherSectionId");
		existingSection.setName("Cultura");

		when(sectionRepository.findByName("Cultura")).thenReturn(Optional.of(existingSection));

		SectionAlreadyExistsException exception = assertThrows(SectionAlreadyExistsException.class,
				() -> specification.validateForUpdate("Cultura", "sectionId"));

		assertEquals("La sección con nombre Cultura ya existe", exception.getMessage());

		verify(sectionRepository).findByName("Cultura");
	}

}