import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type {Book, FiltersGetAllItems as Filters} from "../types";
import type { Paginated } from "@/modules/core/types";
import { fetchAllBooks } from "../service/book.service";

export function useBooks(token: string | null, page: number, filters: Filters) {
    return useQuery<Paginated<Book>>({
        queryKey: ["books", page, filters],
        queryFn: async () => {
            return fetchAllBooks(
                page,
                12,
                filters.nameContains,
                filters.categories,
                token,
            );
        },
        placeholderData: keepPreviousData,
    });
}