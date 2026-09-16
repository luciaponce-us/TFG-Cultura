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
) {
  const [formOverride, setFormOverride] = useState<{
    boardGameId: string | undefined;
    value: BoardGameRequest;
  }>();

  const loadedForm = boardGameToUpdate
    ? toBoardGameRequest(boardGameToUpdate)
    : INITIAL_BOARD_GAME;

  const form =
    formOverride?.boardGameId === boardGameId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<BoardGameRequest>> = (nextForm) => {
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.boardGameId === boardGameId &&
        currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        boardGameId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { form, setForm };
}
