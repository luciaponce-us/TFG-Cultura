import { Grid, Heading, VStack } from "@chakra-ui/react";
import { SideBar } from "@/modules/core/components/SideBar";
import { useEffect, useState } from "react";
import { fetchAllSuggestions } from "../service/suggestion.service";
import { SuggestionCard } from "../components/SuggestionCard";
import type { Suggestion } from "../types";
import { TextSecondary } from "@/modules/core/components/text/TextSecondary";
import type { Paginated } from "@/modules/core/types";
import { CustomPagination } from "@/modules/core/components";

export function SuggestionsPage() {
  const [suggestions, setSuggestions] = useState<Paginated<Suggestion> | null>(null);
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [filters, setFilters] = useState({
    type: undefined,
    text: "",
    orderByCreationDate: false,
    supportedByAdmins: false,
  });

  useEffect(() => {
    async function fetchSuggestions(page: number = 0) {
      setLoading(true);
      try {
        const data = await fetchAllSuggestions(
          page,
          10,
          filters.type,
          filters.text,
          filters.orderByCreationDate,
          filters.supportedByAdmins
        );
        setSuggestions(data);
      } catch (error) {
        console.error("Error fetching suggestions:", error);
        setSuggestions(null);
      } finally {
        setLoading(false);
      }
    }

    fetchSuggestions(page);
  }, [page, filters]);

  function renderSuggestions() {
    if (loading) {
      return <TextSecondary>Cargando sugerencias...</TextSecondary>;
    }

    if (!suggestions || suggestions.content.length === 0) {
      return <TextSecondary>No hay sugerencias disponibles.</TextSecondary>;
    }

    return (
      <VStack align="stretch" gap={4} w="100%">
        {suggestions.content.map((suggestion) => (
          <SuggestionCard key={suggestion.id} suggestion={suggestion} />
        ))}
      </VStack>
    );
  }

  return (
    <Grid
      templateColumns={{ base: "1fr", md: "1fr 2fr" }}
      gap={10}
      flex={1}
      maxW="100vw"
    >
      <SideBar>
        <VStack align="start" gap={4} w="100%">
          <Heading as="h1">Filtros</Heading>
        </VStack>
      </SideBar>
      <VStack
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        align="center"
        justify="flex-start"
        w="100%"
        maxW="800px"
        h="fit-content"
        minH="80vh"
        gap={6}
      >
        <Heading as="h1">Sugerencias</Heading>
        {renderSuggestions()}
        {suggestions && suggestions?.totalPages > 1 && (
          <CustomPagination
            setPage={setPage}
            page={page}
            totalElements={suggestions.totalElements ?? 0}
            size={suggestions.size ?? 10}
          />
        )}
      </VStack>
    </Grid>
  );
}
