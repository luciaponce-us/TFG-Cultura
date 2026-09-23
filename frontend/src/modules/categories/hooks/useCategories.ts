import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Category } from "../types";
import { fetchAllCategories } from "../service/categories.service";

export function useCategories() {
  return useQuery<Category[]>({
    queryKey: ["categories"],
    queryFn: async () => {
      return fetchAllCategories();
    },
    placeholderData: keepPreviousData,
  });
}
