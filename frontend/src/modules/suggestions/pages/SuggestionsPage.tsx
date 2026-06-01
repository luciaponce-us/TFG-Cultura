import { Grid, Heading, Link, VStack } from "@chakra-ui/react";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "@/modules/core/context/useAuth";
import { useNavigate } from "react-router-dom";
import { fetchAllSuggestions } from "../service/suggestion.service";
import type { Suggestion, SuggestionType } from "../types";
import type { Paginated } from "@/modules/core/types";
import {
  SideBar,
  CustomPagination,
  CustomSearchBar,
  CustomSelect,
  TextSecondary,
  CustomButton,
  toaster,
} from "@/modules/core/components";
import { CreateSuggestionDialog, SuggestionCard } from "../components";
import { IconPlus } from "@tabler/icons-react";

interface Filters {
  type?: SuggestionType;
  text: string;
  orderByCreationDate: boolean;
  supportedByAdmins: boolean;
}

const initialFilters: Filters = {
  type: undefined,
  text: "",
  orderByCreationDate: false,
  supportedByAdmins: false,
};

export function SuggestionsPage() {
  const [suggestions, setSuggestions] = useState<Paginated<Suggestion> | null>(
    null,
  );
  const [page, setPage] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [filters, setFilters] = useState<Filters>(initialFilters);
  const [showCreateDialog, setShowCreateDialog] = useState<boolean>(false);
  const { token } = useAuth();
  const navigate = useNavigate();

  const fetchSuggestions = useCallback(
    async (pageToLoad: number = 0) => {
      setLoading(true);
      try {
        const data = await fetchAllSuggestions(
          pageToLoad,
          10,
          filters.type,
          filters.text,
          filters.orderByCreationDate,
          filters.supportedByAdmins,
        );
        setSuggestions(data);
      } catch (error) {
        console.error("Error fetching suggestions:", error);
        setSuggestions(null);
      } finally {
        setLoading(false);
      }
    },
    [
      filters.orderByCreationDate,
      filters.supportedByAdmins,
      filters.text,
      filters.type,
    ],
  );

  useEffect(() => {
    fetchSuggestions(page);
  }, [page, filters, fetchSuggestions]);

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
          <SuggestionCard
            key={suggestion.id}
            suggestion={suggestion}
            onSupportSuccess={() => fetchSuggestions(page)}
          />
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
        <VStack align="start" gap={4} w="100%" minW="210px">
          <Heading as="h1">Filtros</Heading>
          <Link
            variant="underline"
            color="principal.500"
            onClick={() => {
              setPage(0);
              setFilters(initialFilters);
            }}
          >
            Eliminar filtros
          </Link>
          <CustomSearchBar
            placeholder="Buscar..."
            value={filters.text}
            onChange={(e) => {
              setPage(0);
              setFilters({ ...filters, text: e.currentTarget.value });
            }}
          />
          <CustomSelect
            placeholder="Ordenar por"
            options={[
              { label: "Recientes", value: "true" },
              { label: "Más apoyadas", value: "false" },
            ]}
            value={filters.orderByCreationDate ? ["true"] : ["false"]}
            onValueChange={({ value }) => {
              setPage(0);
              setFilters({
                ...filters,
                orderByCreationDate: value[0] === "true",
              });
            }}
            label="Actividad"
          />

          <CustomSelect
            placeholder="Filtrar por tipo"
            options={[
              { label: "Todos (sin filtrar)", value: "" as SuggestionType },
              { label: "Catálogo", value: "CATALOG" as SuggestionType },
              { label: "Eventos", value: "EVENT" as SuggestionType },
              { label: "Otros", value: "OTHER" as SuggestionType },
            ]}
            value={filters.type ? [filters.type] : []}
            onValueChange={({ value }) => {
              setPage(0);
              setFilters({
                ...filters,
                type: (value[0] as SuggestionType) || undefined,
              });
            }}
            label="Tipo"
          />
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
        minW={{ base: "100%", md: "800px" }}
        maxW="800px"
        h="fit-content"
        minH="80vh"
        gap={6}
        flex={1}
      >
        <Heading as="h1">Sugerencias</Heading>
        <CustomButton
          onClick={() => {
            if (!token) {
              toaster.create({
                title: "Inicia sesión para crear sugerencias",
                description: "Serás redirigido a la página de inicio de sesión",
                closable: true,
              });
              navigate("/iniciar-sesion");
            } else {
              setShowCreateDialog(true);
            }
          }}
        >
          <IconPlus />
          Crear sugerencia
        </CustomButton>

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
      {token && (
        <CreateSuggestionDialog
          isOpen={showCreateDialog}
          onClose={() => {
            setShowCreateDialog(false);
            setPage(0);
            fetchSuggestions(0);
          }}
          token={token}
        />
      )}
    </Grid>
  );
}
