import { useMutation, useQueryClient } from "@tanstack/react-query";

import { updateUserAvatar } from "../service/user.service";
import { toaster } from "@/modules/core/components";

interface UpdateUserAvatarParams {
  token: string;
  username: string;
  avatar: File;
}

export function useUpdateUserAvatar() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ token, username, avatar }: UpdateUserAvatarParams) =>
      updateUserAvatar(token, username, avatar),

    onSuccess: async (_, variables) => {
      await queryClient.invalidateQueries({
        queryKey: ["user", variables.username],
      });
    },
    onError: (error: Error) => {
      console.error("Error al actualizar el avatar:", error);

      toaster.create({
        title: "Error",
        description: "No se pudo actualizar el avatar.",
        type: "error",
      });
    },
  });
}
