import { isValidUrl } from "@/modules/core/utils/validations.utils";
import {
  type GameMaster,
  type RolSagaErrors,
  type RolSagaRequest,
} from "../types/rolgame";

export const MAX_LENGTH = {
  NAME: 120,
  DESCRIPTION: 280,
  WEBSITE: 280,
  CHARACTER_SHEET_URL: 280,
  DICE: 100,
  RECOMMENDED_PLAYERS: 50,
};

export function validateRolSagaForm(
  form: RolSagaRequest,
  token?: string | null,
): RolSagaErrors {
  const errors: RolSagaErrors = {
    name: validateName(form.name),
    description: validateDescription(form.description),
    website: validateWebsite(form.website),
    characterSheetUrl: validateCharacterSheetUrl(form.characterSheetUrl),
    dice: validateDice(form.dice),
    recommendedPlayers: validateRecommendedPlayers(form.recommendedPlayers),
    gameMaster: validateGameMaster(form.gameMaster),
    general: !token
      ? "Debes iniciar sesión para crear una saga de juegos de rol."
      : undefined,
  };

  return Object.fromEntries(
    Object.entries(errors).filter(([, error]) => error !== undefined),
  );
}

function validateName(name: string): string | undefined {
  if (!name || name.trim() === "") {
    return "El nombre es obligatorio.";
  }
  if (name.length < 3) {
    return "El nombre debe tener al menos 3 caracteres.";
  }
  if (name.length > MAX_LENGTH.NAME) {
    return `El nombre no puede tener más de ${MAX_LENGTH.NAME} caracteres.`;
  }
  return;
}

function validateDescription(description: string): string | undefined {
  if (!description || description.trim() === "") return "La descripción es obligatoria.";
  if (description.length > MAX_LENGTH.DESCRIPTION) {
    return `La descripción no puede tener más de ${MAX_LENGTH.DESCRIPTION} caracteres.`;
  }
  return;
}

function validateWebsite(website: string): string | undefined {
  if (!website || website.trim() === "") return;
  if (website.length > MAX_LENGTH.WEBSITE) {
    return `El sitio web no puede tener más de ${MAX_LENGTH.WEBSITE} caracteres.`;
  }

  if (!isValidUrl(website)) {
    return "El sitio web debe ser una URL válida.";
  }
  return;
}

function validateCharacterSheetUrl(
  characterSheetUrl: string,
): string | undefined {
  if (!characterSheetUrl || characterSheetUrl.trim() === "") return;
  if (characterSheetUrl.length > MAX_LENGTH.CHARACTER_SHEET_URL) {
    return `La URL de la hoja de personaje no puede tener más de ${MAX_LENGTH.CHARACTER_SHEET_URL} caracteres.`;
  }

  if (!isValidUrl(characterSheetUrl)) {
    return "La URL de la hoja de personaje debe ser una URL válida.";
  }
  return;
}

function validateGameMaster(gameMaster: GameMaster): string | undefined {
  if (!gameMaster || gameMaster.trim() === "") {
    return "El Game Master es obligatorio.";
  }
  return;
}

function validateDice(dice: string): string | undefined {
  if (!dice || dice.trim() === "") return;
  if (dice.length > MAX_LENGTH.DICE) {
    return `Los dados utilizados no pueden tener más de ${MAX_LENGTH.DICE} caracteres.`;
  }
  return;
}

function validateRecommendedPlayers(
  recommendedPlayers: string,
): string | undefined {
  if (!recommendedPlayers || recommendedPlayers.trim() === "") return;
  if (recommendedPlayers.length > MAX_LENGTH.RECOMMENDED_PLAYERS) {
    return `El número de jugadores recomendados no puede tener más de ${MAX_LENGTH.RECOMMENDED_PLAYERS} caracteres.`;
  }
  return;
}
