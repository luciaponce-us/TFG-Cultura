import { keepPreviousData, useQuery } from "@tanstack/react-query";
import type { Book } from "../types/book";
import { fetchAllBooksBySaga } from "../service/book.service";

export function useBooksBySaga(sagaId: string, selfId?: string) {
  return useQuery<Book[]>({
    queryKey: ["books", sagaId, selfId],
    queryFn: async () => {
      const books = await fetchAllBooksBySaga(sagaId);
      if (selfId) {
        return books.filter((book) => book.id !== selfId);
      }
      return books;
    },
    enabled: !!sagaId,
    placeholderData: keepPreviousData,
  });
}
