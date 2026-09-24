import { useBoardGames } from "../../hooks";
import { CreateBoardGameDialog } from "../../components";
import { ItemsPage } from "./ItemsPage";
import { ITEM_TYPES } from "../../types";

export function BoardGamesPage() {
  return (
    <ItemsPage
      getAllHook={useBoardGames}
      title="Juegos de mesa"
      loadText="Cargando juegos de mesa..."
      errorText={{
        title: "Error al cargar los juegos de mesa",
        description:
          "No se pudieron cargar los juegos de mesa. Inténtalo de nuevo más tarde.",
      }}
      emptyText={(hasFilters) =>
        hasFilters
          ? "No hay juegos de mesa que coincidan con los filtros seleccionados."
          : "No hay juegos de mesa disponibles."
      }
      createText="Crear juego de mesa"
      CreateDialogComponent={CreateBoardGameDialog}
      type={ITEM_TYPES.BOARD_GAME}
    />
  );
}
