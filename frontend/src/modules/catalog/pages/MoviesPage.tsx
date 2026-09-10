import { useMovies } from "../hooks";
import { ItemsPage } from "./ItemsPage";
import { CreateMovieDialog } from "../components";

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
      emptyText="No hay películas disponibles."
      createText="Crear película"
      CreateDialogComponent={CreateMovieDialog}
    />
  );
}
