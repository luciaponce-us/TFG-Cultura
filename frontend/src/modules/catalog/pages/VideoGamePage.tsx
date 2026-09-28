import { Box, Grid, HStack, Text } from "@chakra-ui/react";
import { useVideogame } from "../hooks";
import {
  CreateVideoGameDialog,
  ItemTrailer,
} from "../components";
import { useParams } from "react-router-dom";
import { ITEM_TYPES } from "../types";
import { ItemPage } from "./ItemPage";
import { parseDate } from "@/modules/core/utils/utils";
import type { VideoGame } from "../types/videogame";
import { parsePlatform } from "../utils/videogames.utils";

export function VideoGamePage() {
  const { videoGameId } = useParams<{ videoGameId: string }>();
  const { data: videoGame, isLoading, isError } = useVideogame(videoGameId);

  return (
    <ItemPage
      item={videoGame}
      isLoading={isLoading}
      isError={isError}
      itemId={videoGameId ?? ""}
      itemType={ITEM_TYPES.VIDEO_GAME}
      errorMessage="Ha ocurrido un error al cargar el videojuego. Vuelve a intentarlo más tarde."
      CreateItemDialogComponent={CreateVideoGameDialog}
      subtitle={Subtitle({ videoGame })}
      extraInfo={ExtraInfo({ videoGame })}
    />
  );
}

function Subtitle({ videoGame }: { videoGame: VideoGame | undefined }): string | undefined {
  if (!videoGame) return undefined;
  const platform = parsePlatform(videoGame.platform);
  return `Videojuego para ${platform}`;
}

function ExtraInfo({
  videoGame,
}: {
  videoGame: VideoGame | undefined;
}): React.ReactNode | undefined {
  if (!videoGame) return undefined;
  return (
    <>
      <Grid templateColumns={{ base: "1fr", md: "1fr 1.3fr" }} gap={4} w="100%">
        <Box gap={1} display="flex" flexDirection="column">
          <HStack>
            <Text fontWeight="bold">Fecha de lanzamiento:</Text>
            <Text>{parseDate(videoGame.releaseDate)}</Text>
          </HStack>
        </Box>
        {videoGame.trailerUrl && (
          <ItemTrailer item={videoGame} trailerUrl={videoGame.trailerUrl} />
        )}
      </Grid>
    </>
  );
}
