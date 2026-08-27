package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
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
public class ItemRequest {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
	private String name;

	@Size(max = 280, message = "La descripción no puede tener más de 280 caracteres")
	private String description;

	@Builder.Default
	@NotNull(message = "El estado es obligatorio")
	private ItemCondition condition = ItemCondition.PERFECT;

	@Size(max = 280, message = "Los comentarios no pueden tener más de 280 caracteres")
	private String comments;

	@NotNull(message = "La disponibilidad es obligatoria")
	@Builder.Default
	private Boolean loanAvailable = true;

	@Builder.Default
	@NotNull(message = "La visibilidad es obligatoria")
	private Boolean publicated = true;

	@PastOrPresent(message = "La fecha de compra no puede ser futura")
	private LocalDate purchasedAt;

	@DecimalMin(value = "0.00", inclusive = true, message = "El precio debe ser mayor o igual que 0")
	@Digits(integer = 8, fraction = 2, message = "El precio debe tener como máximo 8 enteros y 2 decimales")
	private BigDecimal price;

	@Min(1)
	@Builder.Default
	private Integer copies = 1;

	@Min(0)
	@Builder.Default
	private Integer availableCopies = 1;

	private String sectionId;

	private Set<String> categoriesIds;

}
