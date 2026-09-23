import { useState, type Dispatch, type SetStateAction } from "react";

import {
  INITIAL_BOARD_GAME,
  type BoardGame,
  type BoardGameRequest,
} from "../../types/boardgame";
import { toBoardGameRequest } from "../../utils/item.utils";

export function useBoardGameForm(
  boardGameId: string | undefined,
  boardGameToUpdate?: BoardGame,
  isLoading = false,
) {
  const [formOverride, setFormOverride] = useState<{
    boardGameId: string | undefined;
    value: BoardGameRequest;
  }>();

  const loadedForm = boardGameToUpdate
    ? toBoardGameRequest(boardGameToUpdate)
    : INITIAL_BOARD_GAME;

  const form =
    formOverride !== undefined && formOverride.boardGameId === boardGameId
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<BoardGameRequest>> = (nextForm) => {
    if (boardGameId && isLoading) {
      return; // Esperando a que se cargue el juego de mesa a editar
    }
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride !== undefined &&
        currentOverride.boardGameId === boardGameId
          ? currentOverride.value
          : loadedForm;

      return {
        boardGameId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return {
    form,
    setForm,
  };
}
