import {
  CustomButton,
  CustomSearchBar,
  SideBar,
  TextSecondary,
  toaster,
} from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import { Grid, Heading, Link, VStack } from "@chakra-ui/react";
import { IconPlus } from "@tabler/icons-react";
import { useState } from "react";

import { FILTERS_GET_ALL_ITEMS_DEFAULT } from "../types";

import type { Book, FiltersGetAllItems as Filters } from "../types";
import { useBooks } from "../hooks/useBooks";
import { CreateBookDialog } from "../components";

export function BooksPage() {
  const { token, isAdmin } = useAuth();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(true); // TODO: Change to false when the dialog is implemented
  const [filters, setFilters] = useState<Filters>({
    ...FILTERS_GET_ALL_ITEMS_DEFAULT,
  });
  const [page, setPage] = useState<number>(0);
  const {
    data: paginatedBooks,
    isLoading,
    error,
    isError,
  } = useBooks(token, page, filters);

  const books: Book[] | undefined = paginatedBooks?.content;

  function renderBooks() {
    if (isLoading) {
      return <TextSecondary>Cargando libros...</TextSecondary>;
    }
    if (isError) {
      console.error(error);
      toaster.create({
        title: "Error al cargar los libros",
        description:
          "No se pudieron cargar los libros. Inténtalo de nuevo más tarde.",
        type: "error",
      });
      return <TextSecondary>Error al cargar los libros.</TextSecondary>;
    }

    if (!paginatedBooks || books?.length === 0) {
      return <TextSecondary>No hay libros disponibles.</TextSecondary>;
    }

    return (
      <VStack align="stretch" gap={4} w="100%">
        {books?.map((book) => (
          <TextSecondary key={book.id}>{book.name}</TextSecondary>
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
              setFilters(FILTERS_GET_ALL_ITEMS_DEFAULT);
            }}
          >
            Eliminar filtros
          </Link>
          <CustomSearchBar
            placeholder="Buscar..."
            onChange={(e) => {
              setPage(0);
              setFilters({
                ...FILTERS_GET_ALL_ITEMS_DEFAULT,
                nameContains: e.currentTarget.value,
              });
            }}
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
        <Heading as="h1">Libros</Heading>

        {!isAdmin && ( // TODO: Invert this condition when the dialog is implemented
          <CustomButton
            onClick={() => {
              setIsCreateDialogOpen(true);
            }}
          >
            <IconPlus />
            Crear libro
          </CustomButton>
        )}
        {renderBooks()}
      </VStack>
      <CreateBookDialog
        isOpen={isCreateDialogOpen}
        setIsOpen={setIsCreateDialogOpen}
        token={token}
      />
    </Grid>
  );
}
