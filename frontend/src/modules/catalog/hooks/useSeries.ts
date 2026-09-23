import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Paginated } from "@/modules/core/types";
import type { FiltersGetAllItems as Filters } from "../types";
import type { Series } from "../types/series";
import { fetchAllSeries } from "../service/series.service";

export function useSeries(
  page: number,
  filters: Filters,
  pageSize: number = 12,
) {
  return useQuery<Paginated<Series>>({
    queryKey: ["series", page, filters, pageSize],
    queryFn: async () => {
      return fetchAllSeries(
        page,
        pageSize,
        filters.nameContains,
        filters.categories,
      );
    },
    placeholderData: keepPreviousData,
  });
}
