import { useMutation, useQueryClient } from "@tanstack/react-query";

import { toaster } from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import { isApiError } from "@/modules/core/utils/utils";

import { updateUserProfileAvatar } from "../service/user.service";

interface UpdateUserProfileAvatarParams {
  token: string;
  avatar: File;
  username: string;
}

export function useUpdateUserProfileAvatar() {
  const queryClient = useQueryClient();
  const { setUser } = useAuth();

  return useMutation({
    mutationFn: ({ token, avatar }: UpdateUserProfileAvatarParams) =>
      updateUserProfileAvatar(token, avatar),

    onSuccess: async (updatedUser, variables) => {
      setUser(updatedUser);

      toaster.create({
        title: "Éxito",
        description: "Tu foto de perfil se ha actualizado correctamente.",
        type: "success",
      });

      await queryClient.invalidateQueries({
        queryKey: ["user", variables.username],
      });

      await queryClient.invalidateQueries({
        queryKey: ["users"],
      });
    },
    onError: (error: Error) => {
      console.error("Error al actualizar avatar:", error);
      if (isApiError(error)) {
        toaster.create({
          title: "Error",
          description:
            "No se pudo actualizar la foto de perfil: " + error.message,
          type: "error",
        });
      }
    },
  });
}
