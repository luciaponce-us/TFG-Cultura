import { ITEM_TYPES, type FiltersGetAllItems as Filters } from "../../types";
import type { BookType } from "../../types/book";
import { useBooks } from "../../hooks";
import { CreateBookDialog } from "../../components";
import { ItemsPage } from "./ItemsPage";

const BOOK_TYPES: BookType[] = ["NOVEL", "ENCYCLOPEDIA"];

function useBooksForPage(page: number, filters: Filters) {
  return useBooks(page, filters, BOOK_TYPES);
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
      emptyText={(hasFilters) =>
        hasFilters
          ? "No hay libros que coincidan con los filtros aplicados."
          : "No hay libros disponibles."
      }
      createText="Crear libro"
      CreateDialogComponent={CreateBookDialog}
      sectionDefaultValue="Libros"
      type={ITEM_TYPES.BOOK}
    />
  );
}
