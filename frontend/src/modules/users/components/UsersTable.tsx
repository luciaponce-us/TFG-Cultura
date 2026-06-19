import { HStack, Table } from "@chakra-ui/react";
import type { User } from "../types";
import { CustomAvatar, CustomButton, toaster } from "@/modules/core/components";
import {
  IconEye,
  IconLock,
  IconLockOpen,
  IconPencil,
  IconTrash,
} from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";
import { useState } from "react";
import { deleteUser, toggleUserActive } from "../service/user.service";
import { useAuth } from "@/modules/core/context/useAuth";
import { parsePaymentReceiptUrl, parseRole } from "../utils";
import { useIsMobile } from "@/modules/core/utils/utils";

interface UsersTableProps {
  users: User[];
  fetchUsers: (page: number) => void;
  page: number;
}

export function UsersTable({ users, fetchUsers, page }: UsersTableProps) {
  return (
    <Table.ScrollArea borderWidth="1px" rounded="md" w="100%" overflowX="auto" >
      <Table.Root size="sm"  stickyHeader showColumnBorder>
        <Table.Header>
          <UsersTableHeader />
        </Table.Header>

        <Table.Body>
          {users.map((user) => (
            <UserRow
              key={user.username}
              user={user}
              fetchUsers={fetchUsers}
              page={page}
            />
          ))}
        </Table.Body>
      </Table.Root>
    </Table.ScrollArea>
  );
}

function UsersTableHeader() {
    const isMobile = useIsMobile();
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
  const mobileHeaders = ["Avatar", "Username", "Nombre", "Rol", "Creación", "Acciones"];

  return (
    <Table.Row bg="principal.200">
      {isMobile ? (
        mobileHeaders.map((header) => (
          <Table.ColumnHeader fontWeight="bold" textAlign="center">
            {header}
          </Table.ColumnHeader>
        ))
      ) : (
        headers.map((header) => (
          <Table.ColumnHeader fontWeight="bold" textAlign="center">
            {header}
          </Table.ColumnHeader>
        ))
      )}
    </Table.Row>
  );
}

interface UserRowProps {
  user: User;
  fetchUsers: (page: number) => void;
  page: number;
}

function UserRow({ user, fetchUsers, page }: UserRowProps) {
    const isMobile = useIsMobile();

    const mobileRowsContent = (user: User) => [
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
    {
      key: "created",
      content: new Date(user.createdAt)
        .toLocaleString("es-ES", {
          year: "2-digit",
            month: "2-digit",
            day: "2-digit",
        })
        .replace(",", ""),
    },
    {
      key: "actions",
      content: <UserActions user={user} fetchUsers={fetchUsers} page={page} />,
    },
    ]
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
    {
      key: "actions",
      content: <UserActions user={user} fetchUsers={fetchUsers} page={page} />,
    },
  ];

  return (
    <Table.Row
      key={user.username}
      _hover={{ bg: "principal.50" }}
      cursor="pointer"
    >
      {isMobile? mobileRowsContent(user).map((item)=>(
<Table.Cell
          key={`${user.username}-${item.key}`}
          textAlign="center"
          alignItems="center"
        >
          {item.content}
        </Table.Cell>
      )):
        rowsContent(user).map((item) => (
        <Table.Cell
          key={`${user.username}-${item.key}`}
          textAlign="center"
          alignItems="center"
        >
          {item.content}
        </Table.Cell>
      ))}
    </Table.Row>
  );
}

function UserActions({
  user,
  fetchUsers,
  page,
}: {
  user: User;
  fetchUsers: (page: number) => void;
  page: number;
}) {
  const { token, logout } = useAuth();
  const navigate = useNavigate();
  const [loadingDeleteUsername, setLoadingDeleteUsername] = useState<
    string | null
  >(null);

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
      await toggleUserActive(token, username);
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
