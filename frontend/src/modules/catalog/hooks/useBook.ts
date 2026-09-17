import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { fetchBookById } from "../service/book.service";

export function useBook(bookId: string | undefined) {
  return useQuery({
    queryKey: ["books", bookId],
    queryFn: async () => {
      if (!bookId) {
        return undefined;
      }
      return await fetchBookById(bookId);
    },
    placeholderData: keepPreviousData,
  });
}
