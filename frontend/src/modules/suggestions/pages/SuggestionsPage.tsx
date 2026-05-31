import { Grid, Heading, VStack } from "@chakra-ui/react";
import { SideBar } from "@/modules/core/components/SideBar";
import { useEffect, useState } from "react";
import { fetchAllSuggestions } from "../service/suggestion.service";
import { SuggestionCard } from "../components/SuggestionCard";
import type { Suggestion } from "../types";
import { TextSecondary } from "@/modules/core/components/text/TextSecondary";

export function SuggestionsPage() {
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    async function fetchSuggestions() {
      setLoading(true);
      try {
        const data = await fetchAllSuggestions();
        setSuggestions(data);
      } catch (error) {
        console.error("Error fetching suggestions:", error);
        setSuggestions([]);
      } finally {
        setLoading(false);
      }
    }

    fetchSuggestions();
  }, []);

  function renderSuggestions() {
    if (loading) {
      return <TextSecondary>Cargando sugerencias...</TextSecondary>;
    }

    if (suggestions.length === 0) {
      return <TextSecondary>No hay sugerencias disponibles.</TextSecondary>;
    }

    return (
      <VStack align="stretch" gap={4} w="100%">
        {suggestions.map((suggestion) => (
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
      </VStack>
    </Grid>
  );
}
