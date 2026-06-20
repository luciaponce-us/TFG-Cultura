import { HStack } from "@chakra-ui/react";
import {
  IconLock,
  IconLockOpen,
  IconPencil,
  IconTrash,
} from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";

import { CustomButton, toaster } from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";

import { useDeleteUser, useToggleUserActive } from "../hooks";

import type { User } from "../types";

export function UserActions({ user }: { readonly user: User }) {
  const { token, logout, user: currentUser } = useAuth();
  const navigate = useNavigate();

  const { mutateAsync: deleteUserMutation, isPending: isDeleting } =
    useDeleteUser();

  const { mutateAsync: toggleActiveMutation } = useToggleUserActive();

  async function handleDeleteUser(username: string) {
    if (!token) return;

    try {
      const isCurrentUser = username === currentUser?.username;
      console.log(
        `Eliminando usuario: ${username}, es el usuario actual: ${isCurrentUser}`,
      );

      await deleteUserMutation({
        token,
        username,
      });

      toaster.create({
        title: "Usuario eliminado",
        description: `El usuario ${username} ha sido eliminado exitosamente.`,
        type: "success",
      });

      if (isCurrentUser) {
        logout();
        toaster.create({
          title: "Sesión cerrada",
          description:
            "Tu cuenta ha sido eliminada, por lo que se ha cerrado tu sesión.",
          type: "info",
        });
      }
    } catch {
      toaster.create({
        title: "Error al eliminar usuario",
        description: `No se pudo eliminar el usuario ${username}.`,
        type: "error",
      });
    }
  }

  async function handleToggleActive(username: string, isActive: boolean) {
    if (!token) return;

    try {
      await toggleActiveMutation({
        token,
        username,
      });

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
        } el usuario ${username}.`,
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
        loading={isDeleting}
      >
        <IconTrash size={16} />
      </CustomButton>

      <CustomButton
        color={user.active ? "rojo" : "verde"}
        onClick={() => handleToggleActive(user.username, user.active)}
      >
        {user.active ? <IconLock size={16} /> : <IconLockOpen size={16} />}
      </CustomButton>
    </HStack>
  );
}
