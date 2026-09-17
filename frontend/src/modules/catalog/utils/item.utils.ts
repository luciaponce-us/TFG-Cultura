import type { Item, ItemRequest } from "../types";
import type { BoardGame, BoardGameRequest } from "../types/boardgame";
import type { Book, BookRequest } from "../types/book";

function toRequest(item: Item): ItemRequest {
  return {
    name: item.name,
    description: item.description,
    condition: item.condition,
    comments: item.comments,
    loanAvailable: item.loanAvailable,
    publicated: item.publicated,
    purchasedAt: item.purchasedAt,
    price: item.price,
    copies: item.copies,
    availableCopies: item.availableCopies,
    sectionId: item.section.id,
    categoriesIds: item.categories.map((category) => category.id),
  };
}

export function toBoardGameRequest(boardGame: BoardGame): BoardGameRequest {
  const itemRequest = toRequest(boardGame);
  return {
    ...itemRequest,
    minPlayers: boardGame.minPlayers,
    maxPlayers: boardGame.maxPlayers,
    playTime: boardGame.playTime,
    complexity: boardGame.complexity,
    types: boardGame.types,
    baseGameId: boardGame.baseGame ? boardGame.baseGame.id : undefined,
  };
}

export function toBookRequest(book: Book): BookRequest {
  const itemRequest = toRequest(book);
  return {
    ...itemRequest,
      author: book.author,
      isbn: book.isbn,
      type: book.type,
      sagaName: book.saga? book.saga : undefined,
  };
}