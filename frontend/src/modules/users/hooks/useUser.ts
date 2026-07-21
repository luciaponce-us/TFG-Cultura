import { useQuery } from "@tanstack/react-query";

import { getUserByUsername } from "../service/user.service";

import type { User } from "../types";

export function useUser(token: string | null, username: string | undefined) {
  return useQuery<User>({
    queryKey: ["user", username],
    queryFn: async () => {
      return getUserByUsername(token!, username!);
    },
    enabled: !!token && !!username,
  });
}
