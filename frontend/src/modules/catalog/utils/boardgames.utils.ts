import type { BoardGameType, Complexity } from "../types/boardgame";

export function parseBoardGameTypes(types: BoardGameType[]): string {
  return types.map(parseBoardGameType).join(", ");
}

function parseBoardGameType(type: BoardGameType): string {
  switch (type) {
    case "STRATEGY":
      return "Estrategia";
    case "FAMILY":
      return "Familiar";
    case "PARTY":
      return "Fiesta";
    case "COOPERATIVE":
      return "Cooperativo";
    case "CARD_GAME":
      return "Cartas";
    case "DICE_GAME":
      return "Dados";
    case "MINIATURES":
      return "Miniaturas";
    case "ABSTRACT":
      return "Abstracto";
    case "WARGAME":
      return "Bélico";
  }
}

export function parseComplexity(complexity: Complexity): string {
  switch (complexity) {
    case "EASY":
      return "Fácil";
    case "MEDIUM":
      return "Media";
    case "HARD":
      return "Difícil";
  }
}
