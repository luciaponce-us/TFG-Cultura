import type { GameMaster } from "../types/rolgame";

export function parseGameMaster(gameMaster: GameMaster): string {
  switch (gameMaster) {
    case "COMPULSORY":
      return "Obligatorio";
    case "OPTIONAL":
      return "Opcional";
    case "NO":
      return "No";
    default:
      return "Desconocido";
  }
}
