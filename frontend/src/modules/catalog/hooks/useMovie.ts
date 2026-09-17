import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { fetchMovieById } from "../service/movie.service";

export function useMovie(movieId: string | undefined) {
  return useQuery({
    queryKey: ["movies", movieId],
    queryFn: async () => {
      if (!movieId) {
        return undefined;
      }
      return await fetchMovieById(movieId);
    },
    placeholderData: keepPreviousData,
  });
}
