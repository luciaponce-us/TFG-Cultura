import {
  Flex,
  Grid,
  Heading,
  HStack,
  Link,
  Spinner,
  Table,
  VStack,
} from "@chakra-ui/react";
import {
  CustomButton,
  SideBar,
  CustomPagination,
  CustomAvatar,
  CustomSelect,
  CustomSearchBar,
} from "../../core/components";
import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import type { Role, User } from "../types";
import { useAuth } from "@/modules/core/context/useAuth";
import {
  getAllUsers,
  deleteUser,
  toggleUserActive,
} from "../service/user.service";
import type { Paginated } from "@/modules/core/types";
import {
  IconPencil,
  IconTrash,
  IconLock,
  IconLockOpen,
  IconEye,
} from "@tabler/icons-react";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { parsePaymentReceiptUrl, parseRole } from "../utils";

export default function UsersAdminPage() {
  const { token } = useAuth();
  const { user } = useAuth();
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [paginatedUsers, setPaginatedUsers] = useState<Paginated<User> | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(false);
  const isInitialLoading = loading && !paginatedUsers;
  const [page, setPage] = useState<number>(0);
  const [loadingDeleteUsername, setLoadingDeleteUsername] = useState<
    string | null
  >(null);

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
    fetchUsers(page);
  }, [fetchUsers, page]);

  function renderUsers(users: User[]) {
    function renderActions(user: User) {
      return (
        <HStack>
          <CustomButton
            onClick={() => navigate(`/admin/usuarios/${user.username}`)}
          >
            <IconPencil size={16} />
          </CustomButton>
          <CustomButton
            color="rojo"
            onClick={() => handleDeleteUser(user.username)}
            loading={loadingDeleteUsername === user.username}
          >
            <IconTrash size={16} />
          </CustomButton>
          {user.active ? (
            <CustomButton
              color="rojo"
              onClick={() => handleToggleActive(user.username, user.active)}
            >
              <IconLock size={16} />
            </CustomButton>
          ) : (
            <CustomButton
              color="verde"
              onClick={() => handleToggleActive(user.username, user.active)}
            >
              <IconLockOpen size={16} />
            </CustomButton>
          )}
        </HStack>
      );
    }

    const rowsContent = (user: User) => [
      {
        key: "avatar",
        content: (
          <CustomAvatar
            src={user.avatar || undefined}
            name={user.name}
            w="40px"
            h="40px"
          />
        ),
      },
      { key: "username", content: user.username },
      { key: "fullname", content: `${user.name} ${user.surname}` },
      { key: "role", content: parseRole(user.role) },
      { key: "dni", content: user.dni },
      { key: "phone", content: user.phone },
      { key: "email", content: user.email },
      {
        key: "payment",
        content: user.paymentReceipt ? (
          <CustomButton
            onClick={() =>
              window.open(
                parsePaymentReceiptUrl(user?.paymentReceipt),
                "_blank",
                "noopener,noreferrer",
              )
            }
          >
            <IconEye stroke={2} /> Ver
          </CustomButton>
        ) : (
          "No tiene"
        ),
      },
      { key: "active", content: user.active ? "Sí" : "No" },
      {
        key: "created",
        content: new Date(user.createdAt)
          .toLocaleString("es-ES", {
            year: "numeric",
            month: "2-digit",
            day: "2-digit",
            hour: "2-digit",
            minute: "2-digit",
          })
          .replace(",", ""),
      },
      { key: "actions", content: renderActions(user) },
    ];

    return users.map((user) => (
      <Table.Row
        key={user.username}
        _hover={{ bg: "principal.50" }}
        cursor="pointer"
      >
        {rowsContent(user).map((item) => (
          <Table.Cell
            key={`${user.username}-${item.key}`}
            textAlign="center"
            alignItems="center"
          >
            {item.content}
          </Table.Cell>
        ))}
      </Table.Row>
    ));
  }

  async function handleDeleteUser(username: string) {
    if (!token) return;
    try {
      setLoadingDeleteUsername(username);
      const isCurrentUser = user?.username === username;
      await deleteUser(token, username);
      if (isCurrentUser) {
        logout();
      } else {
        await fetchUsers(page);
      }
      toaster.create({
        title: "Usuario eliminado",
        description: `El usuario ${username} ha sido eliminado exitosamente.`,
        type: "success",
      });
    } catch (error) {
      console.error("Error deleting user:", error);
      toaster.create({
        title: "Error al eliminar usuario",
        description: `No se pudo eliminar el usuario ${username}. Por favor, inténtalo de nuevo.`,
        type: "error",
      });
    } finally {
      setLoadingDeleteUsername(null);
    }
  }

  async function handleToggleActive(username: string, isActive: boolean) {
    if (!token) return;
    try {
      await toggleUserActive(token, username, isActive);
      await fetchUsers(page);
      toaster.create({
        title: `Usuario ${isActive ? "desactivado" : "activado"}`,
        description: `El usuario ${username} ha sido ${
          isActive ? "desactivado" : "activado"
        } exitosamente.`,
        type: "success",
      });
    } catch (error) {
      console.error(
        `Error ${isActive ? "desactivando" : "activando"} usuario:`,
        error,
      );
      toaster.create({
        title: `Error al ${isActive ? "desactivar" : "activar"} usuario`,
        description: `No se pudo ${
          isActive ? "desactivar" : "activar"
        } el usuario ${username}. Por favor, inténtalo de nuevo.`,
        type: "error",
      });
    }
  }

  function renderHeaders() {
    const headers = [
      "Avatar",
      "Username",
      "Nombre",
      "Rol",
      "DNI",
      "Teléfono",
      "Email",
      "Carta de Pago",
      "Activo",
      "Fecha de Creación",
      "Acciones",
    ];

    return (
      <Table.Row bg="principal.200">
        {headers.map((header) => (
          <Table.ColumnHeader fontWeight="bold" textAlign="center">
            {header}
          </Table.ColumnHeader>
        ))}
      </Table.Row>
    );
  }

  return (
    <Grid
      templateColumns={{ base: "1fr", md: "1fr 4fr" }}
      gap={10}
      flex={1}
      maxW="100vw"
    >
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
        h="fit-content"
        minH="80vh"
        gap={6}
      >
        <Heading as="h1">Administración de Usuarios</Heading>
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
              <Table.ScrollArea
                borderWidth="1px"
                rounded="md"
                maxW="80%"
                minW="100%"
              >
                <Table.Root size="sm" stickyHeader showColumnBorder>
                  <Table.Header>{renderHeaders()}</Table.Header>

                  <Table.Body>
                    {renderUsers(paginatedUsers?.content || [])}
                  </Table.Body>
                </Table.Root>
              </Table.ScrollArea>
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
