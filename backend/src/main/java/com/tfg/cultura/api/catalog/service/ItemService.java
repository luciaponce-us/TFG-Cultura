package com.tfg.cultura.api.catalog.service;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ItemService {

    public List<String> getItemTypes() {
        return List.of(
            "Libro",
            "Juego de mesa",
            "Película",
            "Serie",
            "Videojuego",
            "Juego de rol");
    }

}
