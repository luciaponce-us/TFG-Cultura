import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import { updateVideoGame } from "../service/videogame.service";
import type { VideoGameRequest } from "../types/videogame";

export function useUpdateVideogame(
  id: string | undefined,
  request: VideoGameRequest,
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
          title: "Videojuego no válido",
          description:
            "No se proporcionó un ID de videojuego válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar videojuegos",
          description:
            "Necesitas iniciar sesión para actualizar un videojuego.",
          type: "error",
        });
        return;
      }

      return updateVideoGame(token, id, request, image);
    },
    onSuccess: async (videoGames) => {
      toaster.create({
        title: "Videojuego actualizado",
        description: "El videojuego se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["videogames"] });
      setIsOpen(false);
      resetForm();
      return videoGames;
    },
    onError: (error) => {
      console.error("Error al actualizar videojuego:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar videojuego",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar videojuego",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar videojuego",
        description:
          "Ocurrió un error inesperado al actualizar el videojuego. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
