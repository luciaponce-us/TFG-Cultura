import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteUser } from "../service/user.service";
import {
  isApiError,
  isDeactivatedUserError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";

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
    },
    onError: (error) => {
      console.error("Error al eliminar usuario:", error);
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }
    },
  });
}
