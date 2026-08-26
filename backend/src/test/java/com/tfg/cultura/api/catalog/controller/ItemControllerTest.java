package com.tfg.cultura.api.catalog.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tfg.cultura.api.catalog.service.ItemService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class ItemControllerTest extends BaseControllerTest {

    @Mock
    private ItemService itemService;

    private static final String BASE_URL = "/api/catalog";
    private static final String ITEM_TYPES_URL = BASE_URL + "/types";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ItemController controller = new ItemController(itemService);
        mockMvc = buildMockMvc(controller);
    }

    @Test
    void should_return_item_types() throws Exception {
        List<String> itemTypes = List.of("Book", "Movie", "Music");
        when(itemService.getItemTypes()).thenReturn(itemTypes);

        mockMvc.perform(get(ITEM_TYPES_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Book"))
                .andExpect(jsonPath("$[1]").value("Movie"))
                .andExpect(jsonPath("$[2]").value("Music"));
    }
    
}
