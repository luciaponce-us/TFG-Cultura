import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateUser } from "../service/user.service";

import type { UserUpdateRequest } from "../types";

interface UpdateUserParams {
  token: string;
  username: string;
  data: UserUpdateRequest;
}

export function useUpdateUser() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ token, username, data }: UpdateUserParams) =>
      updateUser(token, username, data),

    onSuccess: async (_, variables) => {
      await Promise.all([
        queryClient.invalidateQueries({
          queryKey: ["user", variables.username],
        }),
        queryClient.invalidateQueries({
          queryKey: ["users"],
        }),
      ]);
    },
  });
}
