import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Section } from "../types";
import { fetchAllSections } from "../service/section.service";

export function useSections() {
  return useQuery<Section[]>({
    queryKey: ["sections"],
    queryFn: async () => {
      return fetchAllSections();
    },
    placeholderData: keepPreviousData,
  });
}
