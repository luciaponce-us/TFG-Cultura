import type { Item, ItemErrors, ItemRequest } from "./";
import { INITIAL_ITEM, INITIAL_ITEM_ERRORS } from "./";

export interface BoardGame extends Item {
  minPlayers: number;
  maxPlayers: number;
  playTime: number;
  complexity: Complexity;
  types: BoardGameType[];
  baseGame: BoardGame | null;
  isExpansion: boolean;
}

export const BOARD_GAME_TYPES = [
  "STRATEGY",
  "FAMILY",
  "PARTY",
  "COOPERATIVE",
  "CARD_GAME",
  "DICE_GAME",
  "MINIATURES",
  "ABSTRACT",
  "WARGAME",
] as const;

export type BoardGameType = (typeof BOARD_GAME_TYPES)[number];

export const BOARD_GAME_TYPES_OPTIONS = [
  { value: "STRATEGY", label: "Estrategia" },
  { value: "FAMILY", label: "Familiar" },
  { value: "PARTY", label: "Fiesta" },
  { value: "COOPERATIVE", label: "Cooperativo" },
  { value: "CARD_GAME", label: "Cartas" },
  { value: "DICE_GAME", label: "Dados" },
  { value: "MINIATURES", label: "Miniaturas" },
  { value: "ABSTRACT", label: "Abstracto" },
  { value: "WARGAME", label: "Guerra" },
];

export const COMPLEXITIES = ["EASY", "MEDIUM", "HARD"] as const;

export type Complexity = (typeof COMPLEXITIES)[number];

export const COMPLEXITIES_OPTIONS = [
  { value: "EASY", label: "Fácil" },
  { value: "MEDIUM", label: "Media" },
  { value: "HARD", label: "Difícil" },
];

export interface BoardGameRequest extends ItemRequest {
  minPlayers: number;
  maxPlayers: number;
  playTime: number;
  complexity: Complexity;
  types: BoardGameType[];
  baseGameId?: string;
}

export const INITIAL_BOARD_GAME: BoardGameRequest = {
  ...INITIAL_ITEM,
  minPlayers: 1,
  maxPlayers: 1,
  playTime: 1,
  complexity: "EASY",
  types: [],
  baseGameId: "",
};

export interface BoardGameErrors extends ItemErrors {
  minPlayers?: string;
  maxPlayers?: string;
  playTime?: string;
  complexity?: string;
  types?: string;
  baseGameId?: string;
}

export const INITIAL_BOARD_GAME_ERRORS: BoardGameErrors = {
  ...INITIAL_ITEM_ERRORS,
  minPlayers: "",
  maxPlayers: "",
  playTime: "",
  complexity: "",
  types: "",
  baseGameId: "",
};
