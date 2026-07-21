import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateUser } from "../service/user.service";

import type { UserUpdateRequest } from "../types";
import { useNavigate } from "react-router-dom";
import { toaster } from "@/modules/core/components";
import { isApiError } from "@/modules/core/utils/utils";

interface UpdateUserParams {
  token: string;
  username: string;
  data: UserUpdateRequest;
}

export function useUpdateUser() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: ({ token, username, data }: UpdateUserParams) =>
      updateUser(token, username, data),

    onSuccess: async (updatedUser, variables) => {
      void navigate("/admin/usuarios");

      toaster.create({
        title: "Éxito",
        description: `Usuario "${updatedUser.username}" actualizado correctamente.`,
        type: "success",
      });

      const newUsername = updatedUser.username;
      const oldUsername = variables.username;

      if (newUsername && newUsername !== oldUsername) {
        queryClient.removeQueries({
          queryKey: ["user", oldUsername],
          exact: true,
        });

        await queryClient.invalidateQueries({
          queryKey: ["user", newUsername],
        });
      } else {
        await queryClient.invalidateQueries({
          queryKey: ["user", oldUsername],
        });
      }

      await queryClient.invalidateQueries({
        queryKey: ["users"],
      });
    },
    onError: (error: Error) => {
      console.error("Error al actualizar usuario:", error);
      if (isApiError(error)) {
        toaster.create({
          title: "Error",
          description: "No se pudo actualizar el usuario: " + error.message,
          type: "error",
        });
      } else {
        toaster.create({
          title: "Error",
          description:
            "Ha ocurrido un error inesperado. No se pudo actualizar el usuario.",
          type: "error",
        });
      }
    },
  });
}
