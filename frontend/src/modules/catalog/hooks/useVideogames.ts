import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Paginated } from "@/modules/core/types";
import type { FiltersGetAllItems as Filters } from "../types";
import { fetchAllVideoGames } from "../service/videogame.service";
import type { VideoGame } from "../types/videogame";

export function useVideogames(
  page: number,
  filters: Filters,
  pageSize: number = 12,
) {
  return useQuery<Paginated<VideoGame>>({
    queryKey: ["videogames", page, filters, pageSize],
    queryFn: async () => {
      return fetchAllVideoGames(
        page,
        pageSize,
        filters.nameContains,
        filters.categories,
      );
    },
    placeholderData: keepPreviousData,
  });
}