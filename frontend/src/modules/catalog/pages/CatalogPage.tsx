import {
  Box,
  Heading,
  HStack,
  Separator,
  SimpleGrid,
  Text,
  VStack,
} from "@chakra-ui/react";
import { useBoardGames, useBooks, useMovies, useSeries } from "../hooks";
import { CustomButton } from "@/modules/core/components";
import { useNavigate } from "react-router-dom";
import {
  IconBook,
  IconBooks,
  IconChess,
  IconDeviceTv,
  IconMovie,
} from "@tabler/icons-react";
import { COLORS } from "@/styles/theme";

export function CatalogPage() {
  const navigate = useNavigate();
  const {
    data: books,
    isLoading: isLoadingBooks,
    error: booksError,
  } = useBooks(0, {}, ["NOVEL", "ENCYCLOPEDIA"], 6);
  const {
    data: mangaycomic,
    isLoading: isLoadingMangaYComic,
    error: mangaYComicError,
  } = useBooks(0, {}, ["MANGA", "COMIC"], 6);
  const {
    data: movies,
    isLoading: isLoadingMovies,
    error: moviesError,
  } = useMovies(0, {}, 6);
  const {
    data: series,
    isLoading: isLoadingSeries,
    error: seriesError,
  } = useSeries(0, {}, 6);
  const {
    data: boardgames,
    isLoading: isLoadingBoardgames,
    error: boardgamesError,
  } = useBoardGames(0, {}, 6);

  async function navigateToCatalog(path: string) {
    await navigate(path);
    window.scrollTo({ top: 0, left: 0 });
  }

  function renderItems(
    items: { id: string; name: string }[],
    isLoading: boolean,
    error: unknown,
  ) {
    if (isLoading) {
      return <div>Cargando...</div>;
    }
    if (error) {
      return <div>Error al cargar los elementos</div>;
    }
    if (items.length === 0) {
      return <div>No hay elementos disponibles.</div>;
    }
    return (
      <SimpleGrid columns={6} gap={3} w="100%">
        {items.map((item) => (
          <Box
            key={item.id}
            w="100%"
            aspectRatio={2 / 3}
            border="1px solid"
            borderColor="gray.300"
            display="flex"
            alignItems="center"
            justifyContent="center"
            textAlign="center"
            p={2}
            flexShrink={0}
          >
            <Text
              fontSize={
                item.name.length > 60
                  ? "xs"
                  : item.name.length > 35
                    ? "sm"
                    : "md"
              }
              lineHeight="short"
              overflowWrap="break-word"
              wordBreak="normal"
              hyphens="auto"
              maxW="100%"
            >
              {item.name}
            </Text>
          </Box>
        ))}
      </SimpleGrid>
    );
  }

  const catalogItems = [
    {
      icon: <IconBooks color={COLORS.TEXT_HEADER} size="30px" />,
      title: "Libros",
      link: "/catalogo/libros",
      items: books?.content ?? [],
      isLoading: isLoadingBooks,
      error: booksError,
    },
    {
      icon: <IconBook color={COLORS.TEXT_HEADER} size="30px" />,
      title: "Mangas y Cómics",
      link: "/catalogo/mangas-y-comics",
      items: mangaycomic?.content ?? [],
      isLoading: isLoadingMangaYComic,
      error: mangaYComicError,
    },
    {
      icon: <IconMovie color={COLORS.TEXT_HEADER} size="30px" />,
      title: "Películas",
      link: "/catalogo/peliculas",
      items: movies?.content ?? [],
      isLoading: isLoadingMovies,
      error: moviesError,
    },
    {
      icon: <IconDeviceTv color={COLORS.TEXT_HEADER} size="30px" />,
      title: "Series",
      link: "/catalogo/series",
      items: series?.content ?? [],
      isLoading: isLoadingSeries,
      error: seriesError,
    },
    {
      icon: <IconChess color={COLORS.TEXT_HEADER} size="30px" />,
      title: "Juegos de Mesa",
      link: "/catalogo/juegos-de-mesa",
      items: boardgames?.content ?? [],
      isLoading: isLoadingBoardgames,
      error: boardgamesError,
    },
  ];

  return (
    <VStack
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      align="center"
      justify="flex-start"
      w="100%"
      minW={{ base: "100%", md: "800px" }}
      maxW="800px"
      h="fit-content"
      minH="80vh"
      gap={6}
      flex={1}
    >
      <Heading as="h1">Catálogo</Heading>

      {catalogItems.map((catalogItem) => (
        <VStack key={catalogItem.title} align="start" w="100%" gap={4}>
          <HStack justify="space-between" w="100%" align="center">
            <HStack align="center" gap={2}>
              {catalogItem.icon}
              <Heading as="h2" size="lg" textAlign="start">
                {catalogItem.title}
              </Heading>
            </HStack>
            <CustomButton
              onClick={() => void navigateToCatalog(catalogItem.link)}
            >
              Ver más
            </CustomButton>
          </HStack>
          {renderItems(
            catalogItem.items,
            catalogItem.isLoading,
            catalogItem.error,
          )}
          <Separator
            key={`separator-${catalogItem.title}`}
            w="100%"
            borderColor="gray.300"
          />
        </VStack>
      ))}
    </VStack>
  );
}
