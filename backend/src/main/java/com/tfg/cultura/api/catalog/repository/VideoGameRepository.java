package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.VideoGame;
import com.tfg.cultura.api.catalog.model.enumerators.Platform;

public interface VideoGameRepository extends AbstractItemRepository<VideoGame> {
    boolean existsByNameAndPlatform(String name, Platform platform);
}
