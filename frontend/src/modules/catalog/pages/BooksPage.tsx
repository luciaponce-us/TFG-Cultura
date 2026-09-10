import type { BookType, FiltersGetAllItems as Filters } from "../types";
import { useBooks } from "../hooks/useBooks";
import { CreateBookDialog } from "../components";
import { ItemsPage } from "./ItemsPage";

const BOOK_TYPES: BookType[] = ["NOVEL", "ENCYCLOPEDIA"];

function useBooksForPage(
  token: string | null | undefined,
  page: number,
  filters: Filters,
) {
  return useBooks(token ?? null, page, filters, BOOK_TYPES);
}

export function BooksPage() {
  return (
    <ItemsPage
      getAllHook={useBooksForPage}
      title="Libros"
      loadText="Cargando libros..."
      errorText={{
        title: "Error al cargar los libros",
        description:
          "No se pudieron cargar los libros. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay libros disponibles."
      createText="Crear libro"
      CreateDialogComponent={CreateBookDialog}
    />
  );
}
