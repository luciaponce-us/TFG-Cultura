import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { SeriesRequest } from "../types/series";
import { updateSeries } from "../service/series.service";

export function useUpdateSeries(
  id: string | undefined,
  request: SeriesRequest,
  image: File | null,
  setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void,
  resetForm: () => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!id) {
        toaster.create({
          title: "Serie no válida",
          description:
            "No se proporcionó un ID de serie válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar series",
          description: "Necesitas iniciar sesión para actualizar una serie.",
          type: "error",
        });
        return;
      }

      return updateSeries(token, id, request, image);
    },
    onSuccess: async (series) => {
      toaster.create({
        title: "Serie actualizada",
        description: "La serie se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["series"] });
      setIsOpen(false);
      resetForm();
      return series;
    },
    onError: (error) => {
      console.error("Error al actualizar serie:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar serie",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar serie",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar serie",
        description:
          "Ocurrió un error inesperado al actualizar la serie. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
