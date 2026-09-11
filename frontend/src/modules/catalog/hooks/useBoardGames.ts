import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { FiltersGetAllItems as Filters } from "../types";
import type { BoardGame } from "../types/boardgame";
import type { Paginated } from "@/modules/core/types";
import { fetchAllBoardGames } from "../service/boardgame.service";

export function useBoardGames(page: number, filters: Filters) {
  return useQuery<Paginated<BoardGame>>({
    queryKey: ["boardgames", page, filters],
    queryFn: async () => {
      return fetchAllBoardGames(page, 12, filters.nameContains, filters.categories);
    },
    placeholderData: keepPreviousData,
  });
}