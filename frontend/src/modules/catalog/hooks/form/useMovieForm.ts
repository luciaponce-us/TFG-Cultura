import { useState, type Dispatch, type SetStateAction } from "react";
import { toMovieRequest } from "../../utils/item.utils";
import {
  INITIAL_MOVIE,
  type Movie,
  type MovieRequest,
} from "../../types/movie";

export function useMovieForm(
  movieId: string | undefined,
  movieToUpdate?: Movie,
  isLoading: boolean = false,
): { form: MovieRequest; setForm: Dispatch<SetStateAction<MovieRequest>> } {
  const [formOverride, setFormOverride] = useState<{
    movieId: string | undefined;
    value: MovieRequest;
  }>();

  const loadedForm = movieToUpdate
    ? toMovieRequest(movieToUpdate)
    : INITIAL_MOVIE;

  const form =
    formOverride?.movieId === movieId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<MovieRequest>> = (nextForm) => {
    if (movieId && isLoading) {
      return; // Esperando a que se cargue la película a editar
    }
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.movieId === movieId && currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        movieId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { form, setForm };
}
