import {
  Flex,
  Grid,
  Heading,
  HStack,
  Spinner,
  Table,
  VStack,
} from "@chakra-ui/react";
import { CustomButton, SideBar, CustomPagination } from "../../core/components";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import type { User } from "../types";
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
import { parsePaymentReceiptUrl } from "../utils";

export default function UsersAdminPage() {
  const { token } = useAuth();
  const { user } = useAuth();
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [paginatedUsers, setPaginatedUsers] = useState<Paginated<User> | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(false);
  const [page, setPage] = useState<number>(0);
  const [loadingDeleteUsername, setLoadingDeleteUsername] = useState<
    string | null
  >(null);

  async function fetchUsers(page: number = 0) {
    if (!token) return;
    setLoading(true);
    try {
      const paginatedUsers = await getAllUsers(token, page);
      setPaginatedUsers(paginatedUsers);
    } catch (error) {
      console.error("Error fetching users:", error);
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchUsers();
  }, [token]);

  useEffect(() => {
    fetchUsers(page);
  }, [page, token]);

  function renderUsers(users: User[]) {
    function renderAvatar(user: User) {
      if (user.avatar) {
        return (
          <div style={{ display: "flex", justifyContent: "center" }}>
            <img
              src={user.avatar}
              alt={`${user.name}'s avatar`}
              style={{ width: "40px", height: "40px", borderRadius: "50%" }}
            />
          </div>
        );
      }
    }

    function parseRole(role: string) {
      switch (role) {
        case "COORDINADOR":
          return "Coordinador";
        case "SECRETARIO":
          return "Secretario";
        case "ENCARGADO":
          return "Encargado";
        case "COLABORADOR":
          return "Colaborador";
        case "SOCIO":
          return "Socio";
        default:
          return role;
      }
    }

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
      renderAvatar(user),
      user.username,
      `${user.name} ${user.surname}`,
      parseRole(user.role),
      user.dni,
      user.phone,
      user.email,
      user.paymentReceipt ? <CustomButton
              onClick={() =>
                window.open(
                  parsePaymentReceiptUrl(user?.paymentReceipt as string),
                  "_blank",
                  "noopener,noreferrer",
                )
              }
            >
              <IconEye stroke={2} /> Ver
            </CustomButton> : "No tiene",
      user.active ? "Sí" : "No",
      new Date(user.createdAt)
        .toLocaleString("es-ES", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit",
          hour: "2-digit",
          minute: "2-digit",
        })
        .replace(",", ""),
      renderActions(user),
    ];

    return users.map((user) => (
      <Table.Row
        key={user.username}
        _hover={{ bg: "principal.50" }}
        cursor="pointer"
      >
        {rowsContent(user).map((content, index) => (
          <Table.Cell key={index} textAlign="center" alignItems="center">
            {content}
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
        <VStack align="start" gap={0}>
          <Heading as="h1">Filtros</Heading>
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
        maxW="100vw"
        h="fit-content"
        gap={6}
      >
        <Heading as="h1">Administración de Usuarios</Heading>
        {loading ? (
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
        <CustomPagination {...paginatedUsers} setPage={setPage} page={page} />
      </Flex>
    </Grid>
  );
}
