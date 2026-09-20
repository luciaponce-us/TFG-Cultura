import { useQuery } from "@tanstack/react-query";
import type { RolSaga } from "../types/rolgame";
import { fetchRolSagaById } from "../service/rolsaga.service";

export function useRolSaga(sagaId: string | undefined) {
  return useQuery<RolSaga | undefined>({
    queryKey: ["rolsagas", sagaId],
    queryFn: () => {
      if (!sagaId) {
        return undefined;
      }
      return fetchRolSagaById(sagaId);
    },
    enabled: !!sagaId
  });
}
