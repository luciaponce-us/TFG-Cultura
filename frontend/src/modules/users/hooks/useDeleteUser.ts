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
    onSuccess: () => {
      // invalidamos TODA la lista de usuarios (respeta page + filters automáticamente)
      queryClient.invalidateQueries({
        queryKey: ["users"],
      });
    },
    onError: (error) => {
      console.error("Error deleting user:", error);
    },
  });
}
