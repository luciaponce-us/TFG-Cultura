import { useQuery } from "@tanstack/react-query";

import { fetchSagaByName } from "../service/saga.service";
import type { Saga } from "../types/saga";

export function useSaga(sagaName: string | undefined) {
  return useQuery<Saga | undefined>({
    queryKey: ["sagas", sagaName],
    queryFn: () => {
      if (!sagaName) {
        return undefined;
      }
      return fetchSagaByName(sagaName);
    },
    enabled: !!sagaName,
  });
}
