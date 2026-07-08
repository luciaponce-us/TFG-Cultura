import { Grid, Heading, Link, VStack } from "@chakra-ui/react";
import { IconPlus } from "@tabler/icons-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  CustomButton,
  CustomPagination,
  CustomSearchBar,
  CustomSelect,
  SideBar,
  TextSecondary,
  toaster,
} from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";

import { CreateSuggestionDialog, SuggestionCard } from "../components";
import { useSuggestions } from "../hooks";

import type {
  FiltersGetAllSuggestions as Filters,
  SuggestionType,
} from "../types";

const initialFilters: Filters = {
  type: undefined,
  text: "",
  orderByCreationDate: false,
  supportedByAdmins: false,
};

const suggestionTypeOptions = [
  { label: "Todos (sin filtrar)", value: "" },
  { label: "Catálogo", value: "CATALOG" },
  { label: "Eventos", value: "EVENT" },
  { label: "Otros", value: "OTHER" },
];

export function SuggestionsPage({
  mySuggestions = false,
}: {
  mySuggestions?: boolean;
}) {
  const { token } = useAuth();
  const navigate = useNavigate();

  const [page, setPage] = useState<number>(0);
  const [filters, setFilters] = useState<Filters>(initialFilters);
  const {
    data: paginatedSuggestions,
    isLoading,
    error,
    isError,
  } = useSuggestions(token, page, filters, mySuggestions);

  const suggestions = paginatedSuggestions?.content;

  const [showCreateDialog, setShowCreateDialog] = useState<boolean>(false);

  function renderSuggestions() {
    if (isLoading) {
      return <TextSecondary>Cargando sugerencias...</TextSecondary>;
    }
    if (isError) {
      console.error(error);
      toaster.create({
        title: "Error al cargar sugerencias",
        description:
          "No se pudieron cargar las sugerencias. Inténtalo de nuevo.",
        type: "error",
      });
      return <TextSecondary>Error al cargar sugerencias.</TextSecondary>;
    }

    if (!paginatedSuggestions || suggestions?.length === 0) {
      return <TextSecondary>No hay sugerencias disponibles.</TextSecondary>;
    }

    return (
      <VStack align="stretch" gap={4} w="100%">
        {suggestions?.map((suggestion) => (
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
            options={suggestionTypeOptions}
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
        {mySuggestions ? (
          <Heading as="h1">Mis sugerencias</Heading>
        ) : (
          <Heading as="h1">Sugerencias</Heading>
        )}
        <CustomButton
          onClick={() => {
            if (!token) {
              toaster.create({
                title: "Inicia sesión para crear sugerencias",
                description: "Serás redirigido a la página de inicio de sesión",
                closable: true,
              });
              void navigate("/iniciar-sesion");
            } else {
              setShowCreateDialog(true);
            }
          }}
        >
          <IconPlus />
          Crear sugerencia
        </CustomButton>

        {renderSuggestions()}
        {suggestions && paginatedSuggestions?.totalPages > 1 && (
          <CustomPagination
            setPage={setPage}
            page={page}
            totalElements={paginatedSuggestions.totalElements ?? 0}
            size={paginatedSuggestions.size ?? 3}
          />
        )}
      </VStack>
      {token && (
        <CreateSuggestionDialog
          isOpen={showCreateDialog}
          setIsOpen={setShowCreateDialog}
          token={token}
        />
      )}
    </Grid>
  );
}
