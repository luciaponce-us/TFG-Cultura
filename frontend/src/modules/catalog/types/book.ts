import type { Item, ItemRequest, ItemErrors } from "./";
import { INITIAL_ITEM, INITIAL_ITEM_ERRORS } from "./";

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

export interface BookRequest extends ItemRequest {
  author: string;
  isbn: string;
  type: BookType;
  sagaName?: string;
}

export const INITIAL_BOOK: BookRequest = {
  ...INITIAL_ITEM,
  author: "",
  isbn: "",
  type: "NOVEL",
  sagaName: "",
};

export interface BookErrors extends ItemErrors {
  author?: string;
  isbn?: string;
  type?: string;
  sagaName?: string;
}

export const INITIAL_BOOK_ERRORS: BookErrors = {
  ...INITIAL_ITEM_ERRORS,
  author: "",
  isbn: "",
  type: "",
  sagaName: "",
};
