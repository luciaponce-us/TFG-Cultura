import { Flex, Grid, Heading, Link, Spinner, VStack } from "@chakra-ui/react";
import {
  SideBar,
  CustomPagination,
  CustomSelect,
  CustomSearchBar,
} from "../../core/components";
import { useState, useEffect, useCallback } from "react";
import type { Role, User } from "../types";
import { useAuth } from "@/modules/core/context/useAuth";
import { getAllUsers } from "../service/user.service";
import type { Paginated } from "@/modules/core/types";
import { UsersTable } from "../components/UsersTable";
import { useIsMobile } from "@/modules/core/utils/utils";

export default function UsersAdminPage() {
  const isMobile = useIsMobile();
  const { token } = useAuth();
  const [paginatedUsers, setPaginatedUsers] = useState<Paginated<User> | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(false);
  const isInitialLoading = loading && !paginatedUsers;
  const [page, setPage] = useState<number>(0);

  const [filters, setFilters] = useState({
    name: "",
    role: "",
    active: "",
  });

  const fetchUsers = useCallback(
    async (pageToFetch: number = 0) => {
      if (!token) return;
      setLoading(true);
      try {
        const paginatedUsers = await getAllUsers(
          token,
          pageToFetch,
          10,
          filters.name,
          filters.role,
          filters.active,
        );
        setPaginatedUsers(paginatedUsers);
      } catch (error) {
        console.error("Error fetching users:", error);
      } finally {
        setLoading(false);
      }
    },
    [token, filters],
  );

  useEffect(() => {
    async function fetchData() {
      await fetchUsers(page);
    }
    fetchData();
  }, [fetchUsers, page]);

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
              setFilters({
                name: "",
                role: "",
                active: "",
              });
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
            onValueChange={({ value }) => {
              setPage(0);
              setFilters({ ...filters, active: value[0] || "" });
            }}
            label="Actividad"
          />

          <CustomSelect
            placeholder="Filtrar por rol"
            options={[
              { label: "Socio", value: "SOCIO" as Role },
              { label: "Colaborador", value: "COLABORADOR" as Role },
              { label: "Encargado", value: "ENCARGADO" as Role },
              { label: "Secretario", value: "SECRETARIO" as Role },
              { label: "Coordinador", value: "COORDINADOR" as Role },
            ]}
            value={filters.role ? [filters.role] : []}
            onValueChange={({ value }) => {
              setPage(0);
              setFilters({ ...filters, role: value[0] || "" });
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
              setPage(0);
              setFilters({ ...filters, name: e.currentTarget.value });
            }}
          />
        )}
        {isInitialLoading ? (
          <Spinner size="xl" borderWidth="4px" color="principal.800" />
        ) : (
          <>
            {paginatedUsers?.content.length === 0 &&
            paginatedUsers?.content !== undefined ? (
              <Flex mt={4} color="text.muted">
                No se encontraron usuarios.
              </Flex>
            ) : (
              <UsersTable
                users={paginatedUsers?.content || []}
                fetchUsers={fetchUsers}
                page={page}
              />
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
