package com.tfg.cultura.api.catalog.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import com.tfg.cultura.api.sections.model.Section;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Item {

    private static final String DEFAULT_IMAGE_URL = "https://placehold.net/book-600x800.png";

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    @Size(max = 280, message = "La descripción no puede tener más de 280 caracteres")
    private String description;

    @Pattern(regexp = "^https://res\\.cloudinary\\.com/[^/]+/(image|raw|video)/upload/.+", message = "URL de Cloudinary no válida")
    @Builder.Default
    private String imageUrl= DEFAULT_IMAGE_URL;

    @Builder.Default
    @NotBlank(message = "El estado es obligatorio")
    private ItemCondition condition = ItemCondition.PERFECT;
    
    @Size(max = 280, message = "Los comentarios no pueden tener más de 280 caracteres")
    private String comments;

    @NotNull(message = "La disponibilidad es obligatoria")
    @Builder.Default
    private Boolean loanAvailable = true;

    @Builder.Default
    @NotNull(message = "La visibilidad es obligatoria")
    private Boolean publicated = true;

    private LocalDate purchasedAt;

    private Double price;

    @Min(1)
    @Builder.Default
    private Integer copies = 1;

    @Min(0)
    @Builder.Default
    private Integer availableCopies = 1;

    @Min(0)
    private Integer loanDays;

    @DocumentReference
    private Section section;

    @DocumentReference
    private Set<Category> categories;
    
    @CreatedDate
    private LocalDateTime createdAt;
}
