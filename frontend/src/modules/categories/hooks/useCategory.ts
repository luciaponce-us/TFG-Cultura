import { useQuery } from "@tanstack/react-query";
import type { Category } from "../types";
import { fetchCategoryById } from "../service/categories.service";

export function useCategory(categoryId: string | undefined) {
  return useQuery<Category | undefined>({
    queryKey: ["categories", categoryId],
    queryFn: async () => {
      if (!categoryId) {
        return;
      }
      return fetchCategoryById(categoryId);
    },
    enabled: !!categoryId,
  });
}
