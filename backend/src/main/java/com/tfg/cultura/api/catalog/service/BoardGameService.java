package com.tfg.cultura.api.catalog.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.dto.BoardGameRequest;
import com.tfg.cultura.api.catalog.model.dto.BoardGameResponse;
import com.tfg.cultura.api.catalog.repository.BoardGameRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@Service
public class BoardGameService extends AbstractItemService<BoardGame, BoardGameRepository, BoardGameRequest, BoardGameResponse> {

    public BoardGameService(BoardGameRepository boardGameRepository, SectionService sectionService,
                            CategoryService categoryService, FileService fileService) {
        super(boardGameRepository, sectionService, categoryService, fileService, BoardGameResponse::new);
    }

    @Override
    protected String getImageFolder() {
        return "cultura/items/boardgames";
    }

    @Override
    protected String getDefaultImageUrl() {
        return "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/boardgames_placeholder.jpg";
    }

    @Override
    protected void validate(BoardGame item) {
        checkBaseGameNotSelf(item);
        checkBaseGameHasNoBaseGame(item);
        checkMinMaxPlayers(item);
    }

    private void checkBaseGameNotSelf(BoardGame item) {
        if (item.getBaseGame() != null && item.getBaseGame().getId().equals(item.getId())) {
            throw new IllegalArgumentException("Un juego de mesa no puede ser su propio juego base");
        }
    }

    private void checkBaseGameHasNoBaseGame(BoardGame item) {
        if (item.getBaseGame() != null && item.getBaseGame().getBaseGame() != null) {
            throw new IllegalArgumentException("El juego base no puede ser una expansión de otro juego");
        }
    }

    private void checkMinMaxPlayers(BoardGame item) {
        if (item.getMinPlayers() > item.getMaxPlayers()) {
            throw new IllegalArgumentException("El número mínimo de jugadores no puede ser mayor que el número máximo de jugadores");
        }
    }

    @Override
    protected BoardGame createEntity() {
        return BoardGame.builder().build();
    }

    @Override
    protected void fillSpecificFields(BoardGame item, BoardGameRequest request) {

        BoardGame baseGame = this.findById(request.getBaseGameId());

        item.setMinPlayers(request.getMinPlayers());
        item.setMaxPlayers(request.getMaxPlayers());
        item.setPlayTime(request.getPlayTime());
        item.setComplexity(request.getComplexity());
        item.setTypes(Set.of(request.getTypes()));
        item.setBaseGame(baseGame);
    }

    @Override
    protected Integer getLoanDays(BoardGameRequest request) {
        return 2;
    }
    
}
