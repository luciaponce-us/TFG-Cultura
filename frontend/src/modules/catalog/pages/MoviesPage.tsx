import { useMovies } from "../hooks";
import { ItemsPage } from "./ItemsPage";
import { CreateMovieDialog } from "../components";
import { ITEM_TYPES } from "../types";

export function MoviesPage() {
  return (
    <ItemsPage
      getAllHook={useMovies}
      title="Películas"
      loadText="Cargando películas..."
      errorText={{
        title: "Error al cargar las películas",
        description:
          "No se pudieron cargar las películas. Inténtalo de nuevo más tarde.",
      }}
      emptyText={(hasFilters) =>
        hasFilters
          ? "No hay películas que coincidan con los filtros seleccionados."
          : "No hay películas disponibles."
      }
      createText="Crear película"
      CreateDialogComponent={CreateMovieDialog}
      type={ITEM_TYPES.MOVIE}
    />
  );
}
