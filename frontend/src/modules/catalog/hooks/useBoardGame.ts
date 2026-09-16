import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { fetchBoardGameById } from "../service/boardgame.service";

export function useBoardGame(boardGameId: string | undefined) {
  return useQuery({
    queryKey: ["boardgame", boardGameId],
    queryFn: async () => {
      if (!boardGameId) {
        return undefined;
      }
      return await fetchBoardGameById(boardGameId);
    },
    placeholderData: keepPreviousData,
  });
}
