import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";

import { toaster } from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import {
  isApiError,
  isDeactivatedUserError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";

import { updateUserProfile } from "../service/user.service";

import type { UserProfileUpdateRequest } from "../types";

interface UpdateUserProfileParams {
  token: string;
  data: UserProfileUpdateRequest;
  oldUsername: string;
}

export function useUpdateUserProfile() {
  const queryClient = useQueryClient();
  const { setUser } = useAuth();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: ({ token, data }: UpdateUserProfileParams) =>
      updateUserProfile(token, data),

    onSuccess: async (updatedUser, variables) => {
      setUser(updatedUser);
      void navigate(`/perfil`);

      toaster.create({
        title: "Éxito",
        description: `Tu perfil se ha actualizado correctamente.`,
        type: "success",
      });

      const newUsername = variables.data.username;

      if (newUsername && newUsername !== variables.oldUsername) {
        queryClient.removeQueries({
          queryKey: ["user", variables.oldUsername],
          exact: true,
        });

        await queryClient.invalidateQueries({
          queryKey: ["user", newUsername],
        });
      } else {
        await queryClient.invalidateQueries({
          queryKey: ["user", variables.oldUsername],
        });
      }

      await queryClient.invalidateQueries({
        queryKey: ["users"],
      });
    },
    onError: (error: Error) => {
      console.error("Error al registrar usuario:", error);
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }

      if (isApiError(error)) {
        toaster.create({
          title: "Error",
          description: "No se pudo actualizar el usuario: " + error.message,
          type: "error",
        });
      }
    },
  });
}
