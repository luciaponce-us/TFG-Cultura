import type { FiltersGetAllItems as Filters } from "../types";
import type {BookType} from "../types/book";
import { useBooks } from "../hooks";
import { CreateBookDialog } from "../components";
import { ItemsPage } from "./ItemsPage";

const BOOK_TYPES: BookType[] = ["MANGA", "COMIC"];

function useBooksForPage(
  token: string | null | undefined,
  page: number,
  filters: Filters,
) {
  return useBooks(token ?? null, page, filters, BOOK_TYPES);
}

export function MangasAndComicsPage() {
  return (
    <ItemsPage
      getAllHook={useBooksForPage}
      title="Mangas y cómics"
      loadText="Cargando mangas y cómics..."
      errorText={{
        title: "Error al cargar los mangas y cómics",
        description:
          "No se pudieron cargar los mangas y cómics. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay mangas ni cómics disponibles."
      createText="Crear manga o cómic"
      CreateDialogComponent={CreateBookDialog}
    />
  );
}
