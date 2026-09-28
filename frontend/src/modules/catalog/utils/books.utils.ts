import type { BookType } from "../types/book";

export function parseBookType(type: BookType): string {
  switch (type) {
    case "NOVEL":
      return "Novela";
    case "COMIC":
      return "Cómic";
    case "MANGA":
      return "Manga";
    case "ENCYCLOPEDIA":
      return "Enciclopedia";
  }
}
