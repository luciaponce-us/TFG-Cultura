package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.dto.BoardGameRequest;
import com.tfg.cultura.api.catalog.repository.BoardGameRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class BoardGameServiceTest {

    @Mock
    private BoardGameRepository boardGameRepository;

    @Mock
    private SectionService sectionService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private FileService fileService;

    @InjectMocks
    private BoardGameService service;
    
    private BoardGame boardGame;
    private BoardGame baseGame;
    private BoardGameRequest request;

    @BeforeEach
    void setUp(){
        boardGame = CatalogFactory.validBoardGame();
        baseGame = CatalogFactory.validBaseBoardGame();
        request = CatalogFactory.validBoardGameRequest();
    }

    @Test
    void should_not_throw_when_base_is_null(){
        boardGame.setBaseGame(null);
        assertDoesNotThrow(() -> service.validate(boardGame));
    }

    @Test
    void should_not_throw_when_base_game_not_self(){
        boardGame.setBaseGame(baseGame);
        assertDoesNotThrow(() -> service.validate(boardGame));
    }

    @Test
    void should_throw_when_base_game_is_self(){
        boardGame.setBaseGame(boardGame);
        assertThrows(IllegalArgumentException.class, () -> service.validate(boardGame));
    }

    @Test
    void should_throw_when_base_game_has_base_game(){
        baseGame.setBaseGame(CatalogFactory.validBaseBoardGame());
        boardGame.setBaseGame(baseGame);
        assertThrows(IllegalArgumentException.class, () -> service.validate(boardGame));
    }

    @Test
    void should_not_throw_when_min_players_is_less_than_max_players(){
        boardGame.setMinPlayers(2);
        boardGame.setMaxPlayers(4);
        assertDoesNotThrow(() -> service.validate(boardGame));
    }

    @Test
    void should_not_throw_when_min_players_is_equals_to_max_players(){
        boardGame.setMinPlayers(4);
        boardGame.setMaxPlayers(4);
        assertDoesNotThrow(() -> service.validate(boardGame));
    }

    @Test
    void should_throw_when_min_players_is_greater_than_max_players(){
        boardGame.setMinPlayers(4);
        boardGame.setMaxPlayers(2);
        assertThrows(IllegalArgumentException.class, () -> service.validate(boardGame));
    }

    @Test
    void should_fill_specific_fields_correctly() {
        BoardGame newBoardGame = service.createEntity();
        when(boardGameRepository.findById(request.getBaseGameId())).thenReturn(Optional.of(baseGame));
        service.fillSpecificFields(newBoardGame, request);

        assertDoesNotThrow(() -> service.validate(newBoardGame));
        assertEquals(request.getMinPlayers(), newBoardGame.getMinPlayers());
        assertEquals(request.getMaxPlayers(), newBoardGame.getMaxPlayers());
        assertEquals(request.getPlayTime(), newBoardGame.getPlayTime());
        assertEquals(request.getComplexity(), newBoardGame.getComplexity());
        assertEquals(Set.of(request.getTypes()), newBoardGame.getTypes());
        assertEquals(request.getBaseGameId(), newBoardGame.getBaseGame().getId());
    }

    @Test
    void should_throw_when_base_game_not_found() {
        BoardGame newBoardGame = service.createEntity();
        when(boardGameRepository.findById(request.getBaseGameId())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.fillSpecificFields(newBoardGame, request));
    }

    @Test
    void should_return_loan_days() {
        Integer loanDays = service.getLoanDays(request);
        assertEquals(2, loanDays);
    }
}
