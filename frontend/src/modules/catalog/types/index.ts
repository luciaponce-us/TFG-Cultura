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

export interface ItemCreateRequest {
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

export interface ItemCreateRequestErrors {
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

export const initialItemErrors: ItemCreateRequestErrors = {
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




