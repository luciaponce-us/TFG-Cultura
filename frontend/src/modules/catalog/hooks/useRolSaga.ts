import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { RolSaga } from "../types/rolgame";
import { fetchRolSagaById } from "../service/rolsaga.service";

export function useRolSaga(sagaId: string) {
  return useQuery<RolSaga>({
    queryKey: ["rol-saga", sagaId],
    queryFn: async () => {
      return await fetchRolSagaById(sagaId);
    },
    placeholderData: keepPreviousData,
  });
}
