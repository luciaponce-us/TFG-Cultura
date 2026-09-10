import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { FiltersGetAllItems as Filters } from "../types";
import type { Book, BookType } from "../types/book";
import type { Paginated } from "@/modules/core/types";
import { fetchAllBooks } from "../service/book.service";

export function useBooks(page: number, filters: Filters, types: BookType[]) {
  return useQuery<Paginated<Book>>({
    queryKey: ["books", page, filters],
    queryFn: async () => {
      return fetchAllBooks(
        page,
        12,
        types,
        filters.nameContains,
        filters.categories,
      );
    },
    placeholderData: keepPreviousData,
  });
}
