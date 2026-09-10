import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { SeriesRequest } from "../types/series";
import { createSeries } from "../service/series.service";

export function useCreateSeries(
  seriesData: SeriesRequest,
  image: File | null,
  setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear series",
          description: "Necesitas iniciar sesión para crear una nueva serie.",
          type: "error",
        });
        return;
      }

      await createSeries(token, seriesData, image);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Serie creada",
        description: "La serie se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["series"] });
      setIsOpen(false);
    },
    onError: (error) => {
      console.error("Error al crear serie:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear serie",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear serie",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al crear serie",
        description:
          "Ocurrió un error al crear la serie. Inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}