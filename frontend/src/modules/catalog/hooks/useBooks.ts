import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { FiltersGetAllItems as Filters } from "../types";
import type { Book, BookType } from "../types/book";
import type { Paginated } from "@/modules/core/types";
import { fetchAllBooks } from "../service/book.service";

export function useBooks(
  page: number,
  filters: Filters,
  types: BookType[],
  pageSize: number = 12,
) {
  return useQuery<Paginated<Book>>({
    queryKey: ["books", page, filters, types, pageSize],
    queryFn: async () => {
      return fetchAllBooks(
        page,
        pageSize,
        types,
        filters.nameContains,
        filters.categories,
      );
    },
    placeholderData: keepPreviousData,
  });
}
