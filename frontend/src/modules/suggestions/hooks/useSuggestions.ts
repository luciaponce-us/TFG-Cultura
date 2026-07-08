import { useQuery, keepPreviousData } from "@tanstack/react-query";
import { fetchAllSuggestions } from "../service/suggestion.service";

import type { Paginated } from "@/modules/core/types";
import type { FiltersGetAllSuggestions as Filters, Suggestion } from "../types";

export function useSuggestions(token: string | null, page: number, filters: Filters, mySuggestions: boolean = false) {
  return useQuery<Paginated<Suggestion>>({
    queryKey: ["suggestions", page, filters, mySuggestions],
    queryFn: async () => {
      if (!token) throw new Error("No token");
      
      return fetchAllSuggestions(
        page,
        10,
        filters.type,
        filters.text,
        filters.orderByCreationDate,
        filters.supportedByAdmins,
        mySuggestions,
        token
      );
    },
    enabled: !!token,
    placeholderData: keepPreviousData,
  });
}