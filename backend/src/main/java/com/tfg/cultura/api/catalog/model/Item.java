package com.tfg.cultura.api.catalog.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.core.validation.annotations.ValidCloudinaryUrl;
import com.tfg.cultura.api.core.validation.enums.ResourceType;
import com.tfg.cultura.api.sections.model.Section;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
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

    private static final String DEFAULT_IMAGE_URL = "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/book_placeholder.jpg";

    @Id
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String name;

    @Size(max = 280, message = "La descripción no puede tener más de 280 caracteres")
    private String description;

    @ValidCloudinaryUrl(type = ResourceType.IMAGE, message = "La URL de la imagen no es válida")
    @Builder.Default
    private String imageUrl = DEFAULT_IMAGE_URL;

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

    @PastOrPresent(message = "La fecha de compra debe ser en el pasado o presente")
    private LocalDate purchasedAt;

    @DecimalMin(value = "0.00", inclusive = true, message = "El precio debe ser mayor o igual que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener como máximo 8 enteros y 2 decimales")
    private BigDecimal price;

    @Min(value = 1, message = "Debe haber al menos una copia del ítem")
    @Builder.Default
    private Integer copies = 1;

    @Min(value = 0, message = "El número de copias disponibles no puede ser negativo")
    @Builder.Default
    private Integer availableCopies = 1;

    @Min(value = 0, message = "El número de días para devolver el ítem no puede ser negativo")
    private Integer loanDays;

    @NotNull(message = "El ítem debe pertenecer a una sección")
    @DocumentReference
    private Section section;

    @DocumentReference
    private Set<Category> categories;

    @CreatedDate
    private LocalDateTime createdAt;
}
