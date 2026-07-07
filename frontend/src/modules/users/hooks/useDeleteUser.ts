import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteUser } from "../service/user.service";

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
    },
  });
}
