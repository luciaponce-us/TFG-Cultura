import { CreateSeriesDialog } from "../components";
import { useSeries } from "../hooks";
import { ItemsPage } from "./ItemsPage";

export function SeriesPage() {
  return (
    <ItemsPage
      getAllHook={useSeries}
      title="Series"
      loadText="Cargando series..."
      errorText={{
        title: "Error al cargar las series",
        description:
          "No se pudieron cargar las series. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay series disponibles."
      createText="Crear serie"
      CreateDialogComponent={CreateSeriesDialog}
    />
  );
}
