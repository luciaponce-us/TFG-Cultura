import {
  BOARD_GAME_TYPES,
  COMPLEXITIES,
  type BoardGameErrors,
  type BoardGameRequest,
} from "../types/boardgame";
import { validateItemForm } from "./item.validations";

export function validateBoardGameForm(
  form: BoardGameRequest,
  token?: string | null,
): BoardGameErrors {
  const base = validateItemForm(form, token);

  return {
    ...base,
    minPlayers: validatePositiveInteger(form.minPlayers, "mínimo de jugadores"),
    maxPlayers: validateMaxPlayers(form.minPlayers, form.maxPlayers),
    playTime: validatePositiveInteger(form.playTime, "tiempo de juego"),
    complexity: validateComplexity(form.complexity),
    types: validateTypes(form.types),
    baseGameId: validateBaseGameId(form.baseGameId),
  };
}

function validatePositiveInteger(
  value: number,
  label: string,
): string | undefined {
  if (!Number.isInteger(value) || value < 1) {
    return `El ${label} debe ser al menos 1.`;
  }
  return undefined;
}

function validateComplexity(
  value: BoardGameRequest["complexity"],
): string | undefined {
  return COMPLEXITIES.includes(value)
    ? undefined
    : "La complejidad seleccionada no es válida.";
}

function validateTypes(value: BoardGameRequest["types"]): string | undefined {
  if (!Array.isArray(value) || value.length === 0) {
    return "Debe haber al menos un tipo de juego.";
  }
  if (value.some((type) => !BOARD_GAME_TYPES.includes(type))) {
    return "Hay tipos de juego no válidos.";
  }
  return undefined;
}

function validateMaxPlayers(
  minPlayers: number,
  maxPlayers: number,
): string | undefined {
  const positiveError = validatePositiveInteger(
    maxPlayers,
    "máximo de jugadores",
  );
  if (positiveError) return positiveError;
  if (maxPlayers < minPlayers) {
    return "El máximo de jugadores no puede ser menor que el mínimo.";
  }
  return undefined;
}

function validateBaseGameId(value?: string): string | undefined {
  if (value && value.trim() === "") {
    return "El juego base seleccionado no es válido.";
  }
  return undefined;
}
