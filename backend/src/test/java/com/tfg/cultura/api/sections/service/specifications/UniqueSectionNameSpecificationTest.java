package com.tfg.cultura.api.sections.service.specifications;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.sections.exception.SectionAlreadyExistsException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.repository.SectionRepository;

@ExtendWith(MockitoExtension.class)
class UniqueSectionNameSpecificationTest {

    @Mock
    private SectionRepository sectionRepository;

    private UniqueSectionNameSpecification specification;

    @BeforeEach
    void setUp() {
        specification = new UniqueSectionNameSpecification(sectionRepository);
    }

    @Test
    void should_not_throw_when_section_name_does_not_exist() {
        when(sectionRepository.findByName("Section"))
                .thenReturn(Optional.empty());

        assertDoesNotThrow(() -> specification.validate("Section"));

        verify(sectionRepository).findByName("Section");
    }

    @Test
    void should_throw_when_section_name_already_exists() {
        Section section = new Section();
        section.setName("Section");

        when(sectionRepository.findByName("Section"))
                .thenReturn(Optional.of(section));

        SectionAlreadyExistsException exception = assertThrows(
                SectionAlreadyExistsException.class,
                () -> specification.validate("Section"));

        assertTrue(exception.getMessage().contains("Section"));

        verify(sectionRepository).findByName("Section");
    }
}