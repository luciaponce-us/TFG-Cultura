import { useMutation, useQueryClient } from "@tanstack/react-query";

import { toggleUserActive } from "../service/user.service";

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
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["users"],
      });
    },
  });
}
