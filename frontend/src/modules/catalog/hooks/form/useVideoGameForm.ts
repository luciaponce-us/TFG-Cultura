import { useState, type Dispatch, type SetStateAction } from "react";
import { toVideoGameRequest } from "../../utils/item.utils";
import {
  INITIAL_VIDEO_GAME,
  type VideoGame,
  type VideoGameRequest,
} from "../../types/videogame";

export function useVideoGameForm(
  videoGameId: string | undefined,
  videoGameToUpdate?: VideoGame,
  isLoading: boolean = false,
): {
  form: VideoGameRequest;
  setForm: Dispatch<SetStateAction<VideoGameRequest>>;
} {
  const [formOverride, setFormOverride] = useState<{
    videoGameId: string | undefined;
    value: VideoGameRequest;
  }>();

  const loadedForm = videoGameToUpdate
    ? toVideoGameRequest(videoGameToUpdate)
    : INITIAL_VIDEO_GAME;

  const form =
    formOverride?.videoGameId === videoGameId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<VideoGameRequest>> = (nextForm) => {
    if (videoGameId && isLoading) {
      return; // Esperando a que se cargue el videojuego a editar
    }
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.videoGameId === videoGameId &&
        currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        videoGameId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { form, setForm };
}
