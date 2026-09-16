import { CreateVideoGameDialog } from "../components";
import { useVideogames } from "../hooks";
import { parsePlatform } from "../utils/videogames.utils";
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
      renderItem={(videogame) => (
        <div>
          {videogame.name} - {parsePlatform(videogame.platform)}
        </div>
      )}
      CreateDialogComponent={CreateVideoGameDialog}
      createText="Crear videojuego"
    />
  );
}
