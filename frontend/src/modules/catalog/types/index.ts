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

export interface SectionReference {
  id: string;
  name: string;
}

export interface Category {
  id: string;
  name: string;
  color: string;
}

export const ITEM_CONDITIONS = [
  "PERFECT",
  "MINOR_DAMAGE",
  "MODERATE_DAMAGE",
  "SEVERE_DAMAGE",
] as const;

export type ItemCondition = (typeof ITEM_CONDITIONS)[number];

// BOOK

export interface Book extends Item {
  author: string;
  isbn: string;
  type: BookType;
  saga: string;
}

export const BOOK_TYPES = [
  "NOVEL",
  "COMIC",
  "MANGA",
  "ENCYCLOPEDIA",
  "ROL",
] as const;

export type BookType = (typeof BOOK_TYPES)[number];
