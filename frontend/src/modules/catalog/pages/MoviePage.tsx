import { AspectRatio, Box, Grid, HStack, Text, VStack } from "@chakra-ui/react";
import { useMovie, useMoviesBySaga, useSaga } from "../hooks";
import { CreateMovieDialog, MoreItemsFromSaga } from "../components";
import { useParams } from "react-router-dom";
import { ITEM_TYPES } from "../types";
import { ItemPage } from "./ItemPage";
import { parseDate } from "@/modules/core/utils/utils";
import type { Movie } from "../types/movie";
import type { Saga } from "../types/saga";

export function MoviePage() {
  const { movieId } = useParams<{ movieId: string }>();
  const { data: movie, isLoading, isError } = useMovie(movieId);
  const { data: saga } = useSaga(movie?.saga?.name ?? "");
  console.log("MoviePage saga:", saga);

  return (
    <ItemPage
      item={movie}
      isLoading={isLoading}
      isError={isError}
      itemId={movieId ?? ""}
      itemType={ITEM_TYPES.MOVIE}
      errorMessage="Ha ocurrido un error al cargar la película. Vuelve a intentarlo más tarde."
      CreateItemDialogComponent={CreateMovieDialog}
      subtitle={Subtitle({ movie })}
      extraInfo={ExtraInfo({ movie })}
      sagaComponent={<SagaMovies saga={saga} selfId={movie?.id} />}
      sagaId={movie?.saga?.id}
    />
  );
}

function Subtitle({ movie }: { movie: Movie | undefined }): string | undefined {
  if (!movie || !movie.releaseDate) return undefined;
  const releaseYear = new Date(movie.releaseDate).getFullYear();
  return `Película de ${releaseYear}`;
}

function ExtraInfo({
  movie,
}: {
  movie: Movie | undefined;
}): React.ReactNode | undefined {
  if (!movie) return undefined;
  return (
    <>
      <Grid templateColumns={{base: "1fr", md: "1fr 1.3fr"}} gap={4} w="100%">
        <Box gap={1} display="flex" flexDirection="column">
          <HStack>
            <Text fontWeight="bold">Formato:</Text>
            <Text>{movie.format}</Text>
          </HStack>
          <HStack>
            <Text fontWeight="bold">Número de discos:</Text>
            <Text>{movie.numberOfDiscs}</Text>
          </HStack>
          <HStack>
            <Text fontWeight="bold">Fecha de lanzamiento:</Text>
            <Text>{parseDate(movie.releaseDate)}</Text>
          </HStack>
        </Box>
        {movie.trailerUrl && (
          <VStack align="start" gap={2}>
            <AspectRatio
              ratio={16 / 9}
              w="100%"
              borderRadius="md"
              overflow="hidden"
            >
              <iframe
                src={movie.trailerUrl}
                title={`Trailer de ${movie.name}`}
                allowFullScreen
              />
            </AspectRatio>
          </VStack>
        )}
      </Grid>
    </>
  );
}

function SagaMovies({
  saga,
  selfId,
}: {
  saga: Saga | undefined;
  selfId?: string;
}) {
  const {
    data: sagaMovies,
    isLoading: sagaMoviesLoading,
    isError: sagaMoviesError,
  } = useMoviesBySaga(saga?.id ?? "", selfId);

  if (!saga) return undefined;

  return (
    <MoreItemsFromSaga
      sagaItems={sagaMovies}
      isLoading={sagaMoviesLoading}
      isError={sagaMoviesError}
      CreateItemDialog={CreateMovieDialog}
      itemType={ITEM_TYPES.MOVIE}
      title={`Más películas de la saga "${saga.name}"`}
      loadingText="Cargando películas de la saga..."
      errorText="Ha ocurrido un error al cargar las películas de la saga. Vuelve a intentarlo más tarde."
      emptyText="No hay más películas de esta saga."
    />
  );
}
