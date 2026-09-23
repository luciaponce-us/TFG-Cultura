import { useQuery } from "@tanstack/react-query";
import { fetchRolGameById } from "../service/rolgame.service";

export function useRolGame(rolGameId: string | undefined) {
  return useQuery({
    queryKey: ["rolgames", rolGameId],
    queryFn: async () => {
      if (!rolGameId) {
        return undefined;
      }
      return await fetchRolGameById(rolGameId);
    },
    enabled: !!rolGameId,
  });
}
