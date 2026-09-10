import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Paginated } from "@/modules/core/types";
import type { FiltersGetAllItems as Filters } from "../types";
import type { Series } from "../types/series";
import { fetchAllSeries } from "../service/series.service";

export function useSeries(page: number, filters: Filters) {
  return useQuery<Paginated<Series>>({
    queryKey: ["series", page, filters],
    queryFn: async () => {
      return fetchAllSeries(
        page,
        12,
        filters.nameContains,
        filters.categories,
      );
    },
    placeholderData: keepPreviousData,
  });
}