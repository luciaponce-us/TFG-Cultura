import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { MovieRequest } from "../types/movie";
import { updateMovie } from "../service/movie.service";

export function useUpdateMovie(
  id: string | undefined,
  request: MovieRequest,
  image: File | null,
  setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!id) {
        toaster.create({
          title: "Película no válida",
          description:
            "No se proporcionó un ID de película válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar películas",
          description: "Necesitas iniciar sesión para actualizar una película.",
          type: "error",
        });
        return;
      }

      return updateMovie(token, id, request, image);
    },
    onSuccess: async (movie) => {
      toaster.create({
        title: "Película actualizada",
        description: "La película se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["movies"] });
      setIsOpen(false);
      return movie;
    },
    onError: (error) => {
      console.error("Error al actualizar película:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar película",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar película",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar película",
        description:
          "Ocurrió un error inesperado al actualizar la película. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
