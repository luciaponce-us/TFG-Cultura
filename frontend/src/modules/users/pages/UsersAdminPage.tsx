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
import type { User } from "../types";
import { useAuth } from "@/modules/core/context/useAuth";
import { getAllUsers } from "../service/user.service";
import type { Paginated } from "@/modules/core/types";
import {
  IconPencil,
  IconTrash,
  IconLock,
  IconLockOpen,
} from "@tabler/icons-react";

export default function UsersAdminPage() {
  const { token } = useAuth();
  const [paginatedUsers, setPaginatedUsers] = useState<Paginated<User> | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(false);
  const [page, setPage] = useState<number>(0);

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
          <CustomButton onClick={() => console.log("Editar", user.username)}>
            <IconPencil size={16} />
          </CustomButton>
          <CustomButton
            color="rojo"
            onClick={() => console.log("Eliminar", user.username)}
          >
            <IconTrash size={16} />
          </CustomButton>
          {user.active ? (
            <CustomButton
              color="rojo"
              onClick={() => console.log("Desactivar", user.username)}
            >
              <IconLock size={16} />
            </CustomButton>
          ) : (
            <CustomButton
              color="verde"
              onClick={() => console.log("Activar", user.username)}
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
      user.paymentReceipt ? "Sí" : "No",
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
        gap={4}
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
