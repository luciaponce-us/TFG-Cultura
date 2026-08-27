package com.tfg.cultura.api.catalog.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@InjectMocks
	private ItemService itemService;

	@Test
	void should_return_item_types() {
		var itemTypes = itemService.getItemTypes();

		assert itemTypes.size() == 6;
		assert itemTypes.contains("Libro");
		assert itemTypes.contains("Juego de mesa");
		assert itemTypes.contains("Película");
		assert itemTypes.contains("Serie");
		assert itemTypes.contains("Videojuego");
		assert itemTypes.contains("Juego de rol");
	}

}
