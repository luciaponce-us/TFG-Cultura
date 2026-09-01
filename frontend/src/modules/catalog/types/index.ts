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

export const ITEM_CONDITIONS_OPTIONS = [
  { value: "PERFECT", label: "Perfecto" },
  { value: "MINOR_DAMAGE", label: "Daño menor" },
  { value: "MODERATE_DAMAGE", label: "Daño moderado" },
  { value: "SEVERE_DAMAGE", label: "Daño severo" },
];

export interface ItemCreateRequest {
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

const initialItemErrors: ItemCreateRequestErrors = {
  name: "",
  description: "",
  imageUrl: "",
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

export const BOOK_TYPES_OPTIONS = [
  { value: "NOVEL", label: "Novela" },
  { value: "COMIC", label: "Cómic" },
  { value: "MANGA", label: "Manga" },
  { value: "ENCYCLOPEDIA", label: "Enciclopedia" },
  { value: "ROL", label: "Rol" },
];

export interface BookCreateRequest extends ItemCreateRequest {
  author: string;
  isbn: string;
  type: BookType;
  sagaName: string;
}

export const INITIAL_BOOK: BookCreateRequest = {
  name: "",
  description: "",
  imageUrl: "",
  condition: "PERFECT",
  comments: "",
  loanAvailable: true,
  publicated: true,
  purchasedAt: "",
  price: 0,
  copies: 1,
  availableCopies: 1,
  sectionId: "",
  categoriesIds: [],
  author: "",
  isbn: "",
  type: "NOVEL",
  sagaName: "",
};

export interface BookCreateRequestErrors extends ItemCreateRequestErrors {
  author?: string;
  isbn?: string;
  type?: string;
  sagaName?: string;
}

export const INITIAL_BOOK_ERRORS: BookCreateRequestErrors = {
  ...initialItemErrors,
  author: "",
  isbn: "",
  type: "",
  sagaName: "",
};
