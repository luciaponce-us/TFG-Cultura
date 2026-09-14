import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { RolGame } from "../types/rolgame";
import { fetchAllRolGamesBySagaId } from "../service/rolgame.service";

export function useRolGamesBySaga(sagaId: string) {
  return useQuery<RolGame[]>({
    queryKey: ["rolGamesBySaga", sagaId],
    queryFn: async () => {
      return fetchAllRolGamesBySagaId(sagaId);
    },
    placeholderData: keepPreviousData,
  });
}
