import type { SectionReference } from "@/modules/sections/types";
import { INITIAL_ITEM, INITIAL_ITEM_ERRORS } from "./";
import type { Item, ItemErrors, ItemRequest } from "./";
import type { Category } from "@/modules/categories/types";

export interface RolGame extends Item {
  saga: RolSagaReference;
  type: RolBookType;
}

export const ROL_BOOK_TYPES = ["BASIC", "EXPANSION"] as const;

export type RolBookType = (typeof ROL_BOOK_TYPES)[number];

export const ROL_BOOK_TYPES_OPTIONS = [
  { value: "BASIC", label: "Básico" },
  { value: "EXPANSION", label: "Expansión" },
];

export interface RolSaga {
  id: string;
  imageUrl: string;
  name: string;
  description: string;
  website: string;
  characterSheetUrl: string;
  gameMaster: GameMaster;
  dice: string; // Dados utilizados en la saga (por ejemplo, D20, D6, etc.)
  recommendedPlayers: string;
  section: SectionReference;
  categories: Category[];
  createdAt: string;
}

export interface RolSagaReference {
  id: string;
  imageUrl: string;
  name: string;
}

export const GAME_MASTERS = ["COMPULSORY", "OPTIONAL", "NO"];

export type GameMaster = (typeof GAME_MASTERS)[number];

export const GAME_MASTERS_OPTIONS = [
  { value: "COMPULSORY", label: "Obligatorio" },
  { value: "OPTIONAL", label: "Opcional" },
  { value: "NO", label: "No" },
];

export interface RolGameRequest extends ItemRequest {
  sagaId: string;
  type: RolBookType;
}

export const INITIAL_ROL_GAME: RolGameRequest = {
  ...INITIAL_ITEM,
  sagaId: "",
  type: "BASIC",
};

export interface RolGameErrors extends ItemErrors {
  sagaId?: string;
  type?: string;
}

export const INITIAL_ROL_GAME_ERRORS: RolGameErrors = {
  ...INITIAL_ITEM_ERRORS,
  sagaId: "",
  type: "",
};

export interface RolSagaRequest {
  name: string;
  description: string;
  website: string;
  characterSheetUrl: string;
  gameMaster: GameMaster;
  dice: string; // Dados utilizados en la saga (por ejemplo, D20, D6, etc.)
  recommendedPlayers: string;
  sectionId: string;
  categoriesIds: string[];
}

export const INITIAL_ROL_SAGA: RolSagaRequest = {
  name: "",
  description: "",
  website: "",
  characterSheetUrl: "",
  gameMaster: "COMPULSORY",
  dice: "",
  recommendedPlayers: "",
  sectionId: "",
  categoriesIds: [],
};

export interface RolSagaErrors {
  name?: string;
  description?: string;
  website?: string;
  characterSheetUrl?: string;
  gameMaster?: string;
  dice?: string;
  recommendedPlayers?: string;
  sectionId?: string;
  categoriesIds?: string;
  general?: string;
}

export const INITIAL_ROL_SAGA_ERRORS: RolSagaErrors = {
  name: "",
  description: "",
  website: "",
  characterSheetUrl: "",
  gameMaster: "",
  dice: "",
  recommendedPlayers: "",
  sectionId: "",
  categoriesIds: "",
};
