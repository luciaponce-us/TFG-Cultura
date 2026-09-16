import type { Item, ItemRequest } from "../types";
import type { BoardGame, BoardGameRequest } from "../types/boardgame";

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
      categoriesIds: item.categories.map((category) => category.id)
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
        baseGameId: boardGame.baseGame ? boardGame.baseGame.id : undefined
    };
}