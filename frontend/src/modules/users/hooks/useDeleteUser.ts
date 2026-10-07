import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteUser } from "../service/user.service";
import {
  isApiError,
  isDeactivatedUserError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";
import { toaster } from "@/modules/core/components/toaster/toaster";

type DeleteUserParams = {
  token: string;
  username: string;
};

export function useDeleteUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ token, username }: DeleteUserParams) => {
      return deleteUser(token, username);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["users"],
      });
      toaster.create({
        title: "Usuario eliminado",
        description: "El usuario ha sido eliminado exitosamente.",
        type: "success",
      });
    },
    onError: (error) => {
      console.error("Error al eliminar usuario:", error);
      toaster.create({
        title: "Error",
        description: "No se pudo eliminar el usuario. Por favor, intentálo de nuevo más tarde.",
        type: "error",
      });
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }
    },
  });
}
