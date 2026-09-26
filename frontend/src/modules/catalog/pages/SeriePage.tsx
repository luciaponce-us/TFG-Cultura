import {
  Box,
  Grid,
  Heading,
  HStack,
  Table,
  Text,
  VStack,
} from "@chakra-ui/react";
import { useSerie } from "../hooks";
import { CreateBookDialog, ItemTrailer } from "../components";
import { useParams } from "react-router-dom";
import { ITEM_TYPES } from "../types";
import { ItemPage } from "./ItemPage";
import type { Series } from "../types/series";
import { parseDate } from "@/modules/core/utils/utils";
import { getTrailers, parseStatus } from "../utils/series.utils";

export function SeriePage() {
  const { serieId } = useParams<{ serieId: string }>();
  const { data: serie, isLoading, isError } = useSerie(serieId);

  return (
    <ItemPage
      item={serie}
      isLoading={isLoading}
      isError={isError}
      itemId={serieId ?? ""}
      itemType={ITEM_TYPES.SERIES}
      errorMessage="Ha ocurrido un error al cargar la serie. Vuelve a intentarlo más tarde."
      CreateItemDialogComponent={CreateBookDialog}
      subtitle={Subtitle({ serie })}
      extraInfo={ExtraInfo({ serie })}
    />
  );
}

function Subtitle({
  serie,
}: {
  serie: Series | undefined;
}): string | undefined {
  if (!serie || !serie.releaseDate) return undefined;
  const releaseYear = new Date(serie.releaseDate).getFullYear();
  return `Serie de ${releaseYear}`;
}

function ExtraInfo({
  serie,
}: {
  serie: Series | undefined;
}): React.ReactNode | undefined {
  if (!serie) return undefined;
  const trailers: string[] = getTrailers(serie);

  return (
    <>
      <Grid templateColumns={{ base: "1fr", md: "1fr 1fr" }} gap={4} w="100%">
        <Box gap={1} display="flex" flexDirection="column">
          <HStack>
            <Text fontWeight="bold">Lanzamiento:</Text>
            <Text>{parseDate(serie.releaseDate)}</Text>
          </HStack>
          <HStack>
            <Text fontWeight="bold">Estado:</Text>
            <Text>{parseStatus(serie.status)}</Text>
          </HStack>

          <HStack>
            <Text fontWeight="bold">Formato:</Text>
            <Text>{serie.format}</Text>
          </HStack>
          <HStack>
            <Text fontWeight="bold">Número de discos:</Text>
            <Text>{serie.numberOfDiscs}</Text>
          </HStack>
        </Box>
        <VStack align="stretch" w="100%">
          <Heading size="sm">Temporadas</Heading>
          <Text>
            De un total de {serie.numberOfSeasons} temporadas, este ítem
            contiene:
          </Text>

          <Table.Root
            size="sm"
            variant="outline"
            borderRadius="md"
            overflow="hidden"
          >
            <Table.Header bgColor="principal.100">
              <Table.Row>
                <Table.ColumnHeader w="1%" whiteSpace="nowrap">
                  Temporada
                </Table.ColumnHeader>
                <Table.ColumnHeader w="1%" whiteSpace="nowrap">
                  Parte
                </Table.ColumnHeader>
              </Table.Row>
            </Table.Header>
            <Table.Body>
              {serie.seasons.length > 0 ? (
                serie.seasons.map((season) => (
                  <Table.Row
                    key={`${season.seasonNumber}-${season.seasonPart ?? ""}`}
                    borderColor="principal.800"
                  >
                    <Table.Cell w="1%" whiteSpace="nowrap">
                      {season.seasonNumber}
                    </Table.Cell>
                    <Table.Cell w="1%" whiteSpace="nowrap">
                      {season.seasonPart ?? "-"}
                    </Table.Cell>
                  </Table.Row>
                ))
              ) : (
                <Table.Row borderColor="principal.800">
                  <Table.Cell colSpan={2} borderColor="principal.800">
                    No hay temporadas.
                  </Table.Cell>
                </Table.Row>
              )}
            </Table.Body>
          </Table.Root>
        </VStack>
        {trailers.length > 0 && (
          <VStack gridColumn={{ base: "auto", md: "1 / -1" }}>
            <Heading size="sm">Tráilers</Heading>
            <Box
              direction={{ base: "column", md: "row" }}
              display="flex"
              gap={2}
              flexWrap="wrap"
              w="100%"
              justifyContent="center"
              alignItems="center"
            >
              {trailers.map((trailer, index) => (
                <ItemTrailer key={index} trailerUrl={trailer} item={serie} />
              ))}
            </Box>
          </VStack>
        )}
      </Grid>
    </>
  );
}
