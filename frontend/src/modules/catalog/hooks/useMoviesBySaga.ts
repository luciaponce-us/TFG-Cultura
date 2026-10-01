import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Movie } from "../types/movie";
import { fetchAllMoviesBySaga } from "../service/movie.service";

export function useMoviesBySaga(sagaId: string, selfId?: string) {
  return useQuery<Movie[]>({
    queryKey: ["movies", sagaId, selfId],
    queryFn: async () => {
      const movies = await fetchAllMoviesBySaga(sagaId);
      if (selfId) {
        return movies.filter((movie) => movie.id !== selfId);
      }
      return movies;
    },
    enabled: !!sagaId,
    placeholderData: keepPreviousData,
  });
}
