import type { Item, ItemCreateRequest, ItemCreateRequestErrors } from "./";
import { initialItemErrors } from "./";

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
  sagaName?: string;
}

export const INITIAL_BOOK: BookCreateRequest = {
  name: "",
  description: "",
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
