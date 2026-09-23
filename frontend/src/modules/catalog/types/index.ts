import type { Category } from "@/modules/categories/types";
import type { SectionReference } from "@/modules/sections/types";

export interface FiltersGetAllItems {
  nameContains?: string;
  categories?: string[];
}

export const FILTERS_GET_ALL_ITEMS_DEFAULT: FiltersGetAllItems = {
  nameContains: "",
  categories: [],
};

export interface Item {
  id: string;
  name: string;
  description: string;
  imageUrl: string;
  condition: ItemCondition;
  comments: string;
  loanAvailable: boolean;
  publicated: boolean;
  purchasedAt: string; // LocalDate
  price: number; // BigDecimal
  copies: number; // Integer
  availableCopies: number; // Integer
  loanDays: number; // Integer
  section: SectionReference;
  categories: Category[];
  createdAt: string; // LocalDateTime
}

export const ITEM_CONDITIONS = [
  "PERFECT",
  "MINOR_DAMAGE",
  "MODERATE_DAMAGE",
  "SEVERE_DAMAGE",
] as const;

export type ItemCondition = (typeof ITEM_CONDITIONS)[number];

export const ITEM_CONDITIONS_OPTIONS = [
  { value: "PERFECT", label: "Perfecto" },
  { value: "MINOR_DAMAGE", label: "Daño menor" },
  { value: "MODERATE_DAMAGE", label: "Daño moderado" },
  { value: "SEVERE_DAMAGE", label: "Daño severo" },
];

export const INITIAL_ITEM: ItemRequest = {
  name: "",
  description: "",
  condition: "PERFECT",
  comments: "",
  loanAvailable: true,
  publicated: true,
  purchasedAt: new Date().toISOString().split("T")[0],
  price: 0,
  copies: 1,
  availableCopies: 1,
  sectionId: "",
  categoriesIds: [],
};

export interface ItemRequest {
  name: string;
  description: string;
  condition: ItemCondition;
  comments: string;
  loanAvailable: boolean;
  publicated: boolean;
  purchasedAt: string; // LocalDate
  price: number; // BigDecimal
  copies: number; // Integer
  availableCopies: number; // Integer
  sectionId: string;
  categoriesIds: string[];
}

export interface ItemErrors {
  name?: string;
  description?: string;
  imageUrl?: string;
  condition?: string;
  comments?: string;
  loanAvailable?: string;
  publicated?: string;
  purchasedAt?: string; // LocalDate
  price?: string; // BigDecimal
  copies?: string; // Integer
  availableCopies?: string; // Integer
  sectionId?: string;
  categoriesIds?: string;
  general?: string;
}

export const INITIAL_ITEM_ERRORS: ItemErrors = {
  name: "",
  description: "",
  condition: "",
  comments: "",
  loanAvailable: "",
  publicated: "",
  purchasedAt: "",
  price: "",
  copies: "",
  availableCopies: "",
  sectionId: "",
  categoriesIds: "",
};

export interface ItemRoutes {
  BASE: string;
  GET_ALL_BY_TYPE?: (types: string[]) => string;
  GET_BY_ID: (id: string) => string;
  GET_BY_SAGA_ID?: (sagaId: string) => string;
}

export type { CreateItemDialogProps } from "./props";

export const ITEM_TYPES = {
  BOARD_GAME: "BOARD_GAME",
  BOOK: "BOOK",
  MOVIE: "MOVIE",
  SERIES: "SERIES",
  VIDEO_GAME: "VIDEO_GAME",
  ROL_GAME: "ROL_GAME",
} as const;

export type ItemType = (typeof ITEM_TYPES)[keyof typeof ITEM_TYPES];

export function getItemTypeUrl(type: ItemType): string {
  switch (type) {
    case ITEM_TYPES.BOARD_GAME:
      return "juegos-de-mesa";
    case ITEM_TYPES.BOOK:
      return "libros";
    case ITEM_TYPES.MOVIE:
      return "peliculas";
    case ITEM_TYPES.SERIES:
      return "series";
    case ITEM_TYPES.VIDEO_GAME:
      return "videojuegos";
    case ITEM_TYPES.ROL_GAME:
      return "rol";
  }
}
