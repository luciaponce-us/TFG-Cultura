import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Saga } from "../types";
import { fetchAllSagas } from "../service/saga.service";

export function useSagas() {
  return useQuery<Saga[]>({
    queryKey: ["sagas"],
    queryFn: async () => {
      return await fetchAllSagas();
    },
    placeholderData: keepPreviousData,
  });
}
