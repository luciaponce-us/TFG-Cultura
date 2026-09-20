import { CreateVideoGameDialog } from "../components";
import { useVideogames } from "../hooks";
import { ITEM_TYPES } from "../types";
import { ItemsPage } from "./ItemsPage";

export function VideoGamesPage() {
  return (
    <ItemsPage
      getAllHook={useVideogames}
      title="Videojuegos"
      loadText="Cargando videojuegos..."
      errorText={{
        title: "Error al cargar los videojuegos",
        description:
          "No se pudieron cargar los videojuegos. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay videojuegos disponibles."
      CreateDialogComponent={CreateVideoGameDialog}
      createText="Crear videojuego"
      type={ITEM_TYPES.VIDEO_GAME}
    />
  );
}
