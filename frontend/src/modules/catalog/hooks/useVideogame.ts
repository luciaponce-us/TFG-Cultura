import { useQuery } from "@tanstack/react-query";

import { fetchVideoGameById } from "../service/videogame.service";

export function useVideogame(videoGameId: string | undefined) {
  return useQuery({
    queryKey: ["videogames", videoGameId],
    queryFn: async () => {
      if (!videoGameId) {
        return undefined;
      }
      return await fetchVideoGameById(videoGameId);
    },
    enabled: !!videoGameId,
  });
}
