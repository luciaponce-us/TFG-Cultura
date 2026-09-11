import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { FiltersGetAllItems as Filters } from "../types";
import type { Movie } from "../types/movie";
import type { Paginated } from "@/modules/core/types";
import { fetchAllMovies } from "../service/movie.service";

export function useMovies(
  page: number,
  filters: Filters,
  pageSize: number = 12,
) {
  return useQuery<Paginated<Movie>>({
    queryKey: ["movies", page, filters, pageSize],
    queryFn: async () => {
      return fetchAllMovies(
        page,
        pageSize,
        filters.nameContains,
        filters.categories,
      );
    },
    placeholderData: keepPreviousData,
  });
}
