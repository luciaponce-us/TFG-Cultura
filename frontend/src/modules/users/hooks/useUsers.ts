import { useQuery, keepPreviousData } from "@tanstack/react-query";

import { getAllUsers } from "../service/user.service";

import type { Paginated } from "@/modules/core/types";
import type { FiltersGetAllUsers as Filters, User } from "../types";

export function useUsers(token: string | null, page: number, filters: Filters) {
  return useQuery<Paginated<User>>({
    queryKey: ["users", page, filters],
    queryFn: async () => {
      if (!token) throw new Error("No token");

      return getAllUsers(
        token,
        page,
        10,
        filters.name,
        filters.role,
        filters.active,
      );
    },
    enabled: !!token,
    placeholderData: keepPreviousData,
  });
}
