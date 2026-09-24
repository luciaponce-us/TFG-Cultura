import { ITEM_TYPES, type FiltersGetAllItems as Filters } from "../../types";
import { useBooks } from "../../hooks";
import { CreateBookDialog } from "../../components";
import { ItemsPage } from "./ItemsPage";

function useBooksForPage(page: number, filters: Filters) {
  return useBooks(page, filters, ["MANGA", "COMIC"]);
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
      emptyText={(hasFilters) =>
        hasFilters
          ? "No hay mangas ni cómics que coincidan con los filtros seleccionados."
          : "No hay mangas ni cómics disponibles."
      }
      createText="Crear manga o cómic"
      CreateDialogComponent={CreateBookDialog}
      sectionDefaultValue="Manga"
      type={ITEM_TYPES.BOOK}
    />
  );
}
