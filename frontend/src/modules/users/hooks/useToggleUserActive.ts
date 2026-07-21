import { useMutation, useQueryClient } from "@tanstack/react-query";

import { toggleUserActive } from "../service/user.service";
import {
  isApiError,
  isDeactivatedUserError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";

export function useToggleUserActive() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      token,
      username,
    }: {
      token: string;
      username: string;
    }) => {
      return toggleUserActive(token, username);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["users"],
      });
    },
    onError: (error) => {
      console.error("Error al cambiar estado de usuario:", error);
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }
    },
  });
}
