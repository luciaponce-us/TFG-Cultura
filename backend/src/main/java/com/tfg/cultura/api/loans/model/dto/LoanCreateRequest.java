package com.tfg.cultura.api.loans.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanCreateRequest {

	@NotNull(message = "El ítem es obligatorio")
	@NotBlank(message = "El ítem es obligatorio")
	private String itemId;

	@NotNull(message = "El tipo de ítem es obligatorio")
	private ItemType itemType;
}
