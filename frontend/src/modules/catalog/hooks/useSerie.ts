import { useQuery } from "@tanstack/react-query";

import { fetchSeriesById } from "../service/series.service";

export function useSerie(seriesId: string | undefined) {
  return useQuery({
    queryKey: ["series", seriesId],
    queryFn: async () => {
      if (!seriesId) {
        return undefined;
      }
      return await fetchSeriesById(seriesId);
    },
    enabled: !!seriesId,
  });
}
