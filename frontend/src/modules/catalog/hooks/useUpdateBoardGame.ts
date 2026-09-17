import { useAuth } from "@/modules/core/context/useAuth";
import type { BoardGameRequest } from "../types/boardgame";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { updateBoardGame } from "../service/boardgame.service";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

export function useUpdateBoardGame(
  id: string | undefined,
  request: BoardGameRequest,
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
          title: "Juego de mesa no válido",
          description:
            "No se proporcionó un ID de juego de mesa válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar juegos de mesa",
          description:
            "Necesitas iniciar sesión para actualizar un juego de mesa.",
          type: "error",
        });
        return;
      }

      return updateBoardGame(token, id, request, image);
    },
    onSuccess: async (boardGame) => {
      toaster.create({
        title: "Juego de mesa actualizado",
        description: "El juego de mesa se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["boardgames"] });
      await queryClient.invalidateQueries({ queryKey: ["boardgames", id] });
      setIsOpen(false);
      return boardGame;
    },
    onError: (error) => {
      console.error("Error al actualizar juego de mesa:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar juego de mesa",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar juego de mesa",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar juego de mesa",
        description:
          "Ocurrió un error inesperado al actualizar el juego de mesa. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
