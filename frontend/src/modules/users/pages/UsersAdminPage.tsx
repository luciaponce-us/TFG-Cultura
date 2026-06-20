import { Flex, Grid, Heading, Link, Spinner, VStack } from "@chakra-ui/react";
import { useEffect, useState } from "react";

import { useAuth } from "@/modules/core/context/useAuth";
import { useIsMobile } from "@/modules/core/utils/utils";
import {
  SideBar,
  CustomPagination,
  CustomSearchBar,
  CustomSelect,
  toaster,
} from "@/modules/core/components";

import { UsersTable } from "../components/UsersTable";
import { useUsers } from "../hooks/useUsers";

import type { FiltersGetAllUsers as Filters } from "../types";

export default function UsersAdminPage() {
  const isMobile = useIsMobile();
  const { token } = useAuth();

  const [page, setPage] = useState<number>(0);

  const [filters, setFilters] = useState<Filters>({
    name: "",
    role: "",
    active: "",
  });

  const {
    data: paginatedUsers,
    isLoading, // Primer fetch
    error,
    isError,
  } = useUsers(token, page, filters);

  useEffect(() => {
    if (isError && error) {
      console.error(error);

      toaster.create({
        title: "Error al cargar usuarios",
        description: "No se pudieron cargar los usuarios. Inténtalo de nuevo.",
        type: "error",
      });
    }
  }, [isError, error]);

  function updateFilter(key: keyof Filters, value: Filters[typeof key]) {
    setPage(0);
    setFilters((prev) => ({ ...prev, [key]: value }));
  }

  function resetFilters() {
    setPage(0);
    setFilters({
      name: "",
      role: "",
      active: "",
    });
  }

  return (
    <Grid templateColumns={{ base: "1fr", md: "1fr 4fr" }} gap={10} w="100%">
      <SideBar>
        <VStack align="start" gap={4} w="100%">
          <Heading as="h1">Filtros</Heading>
          <Link
            variant="underline"
            color="principal.500"
            onClick={() => {
              setPage(0);
              resetFilters();
            }}
          >
            Eliminar filtros
          </Link>
          <CustomSearchBar
            placeholder="Buscar por nombre..."
            value={filters.name}
            onChange={(e) => {
              setPage(0);
              setFilters({ ...filters, name: e.currentTarget.value });
            }}
          />
          <CustomSelect
            placeholder="Filtrar por actividad"
            options={[
              { label: "Activo", value: "true" },
              { label: "Inactivo", value: "false" },
            ]}
            value={filters.active ? [filters.active] : []}
            onValueChange={({ value }) =>
              updateFilter("active", value[0] || "")
            }
            label="Actividad"
          />

          <CustomSelect
            placeholder="Filtrar por rol"
            options={[
              { label: "Socio", value: "SOCIO" },
              { label: "Colaborador", value: "COLABORADOR" },
              { label: "Encargado", value: "ENCARGADO" },
              { label: "Secretario", value: "SECRETARIO" },
              { label: "Coordinador", value: "COORDINADOR" },
            ]}
            value={filters.role ? [filters.role] : []}
            onValueChange={({ value }) => {
              updateFilter("role", value[0] || "");
            }}
            label="Rol"
          />
        </VStack>
      </SideBar>
      <Flex
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        direction="column"
        align="center"
        justify="flex-start"
        w="100%"
        minW={0}
        h="fit-content"
        minH="80vh"
        gap={6}
      >
        <Heading as="h1" textAlign="center">
          Administración de Usuarios
        </Heading>
        {isMobile && (
          <CustomSearchBar
            placeholder="Buscar por nombre..."
            value={filters.name}
            onChange={(e) => {
              updateFilter("name", e.currentTarget.value);
            }}
          />
        )}
        {isLoading ? (
          <Spinner size="xl" borderWidth="4px" color="principal.800" />
        ) : (
          <>
            {paginatedUsers?.content.length === 0 &&
            paginatedUsers?.content !== undefined ? (
              <Flex mt={4} color="text.muted">
                No se encontraron usuarios.
              </Flex>
            ) : (
              <UsersTable users={paginatedUsers?.content || []} />
            )}
          </>
        )}
        {paginatedUsers && paginatedUsers.totalPages > 1 && (
          <Flex mt="auto" w="100%" justify="center">
            <CustomPagination
              {...paginatedUsers}
              setPage={setPage}
              page={page}
            />
          </Flex>
        )}
      </Flex>
    </Grid>
  );
}
