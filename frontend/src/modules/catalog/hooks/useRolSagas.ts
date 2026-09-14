import type { Paginated } from "@/modules/core/types";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { RolSaga } from "../types/rolgame";
import { fetchAllRolSagas } from "../service/rolgame.service";

export function useRolSagas(page: number = 0, size: number = 10) {
    return useQuery<Paginated<RolSaga>>({
        queryKey: ["rol-sagas", page, size],
        queryFn: async () => {
            return await fetchAllRolSagas(page, size);
        },
        placeholderData: keepPreviousData
    });
}