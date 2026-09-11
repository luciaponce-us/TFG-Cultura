import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { BoardGameRequest } from "../types/boardgame";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { createBoardGame } from "../service/boardgame.service";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

export function useCreateBoardGame(
  boardGameData: BoardGameRequest,
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
          title: "Inicia sesión para crear juegos de mesa",
          description:
            "Necesitas iniciar sesión para crear un nuevo juego de mesa.",
          type: "error",
        });
        return;
      }

      return createBoardGame(token, boardGameData, image);
    },
    onSuccess: async (boardGame) => {
      toaster.create({
        title: "Juego de mesa creado",
        description: "El juego de mesa se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["boardgames"] });
      setIsOpen(false);
      return boardGame;
    },
    onError: (error) => {
      console.error("Error al crear juego de mesa:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear juego de mesa",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear juego de mesa",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al crear juego de mesa",
        description:
          "Ocurrió un error al crear el juego de mesa. Inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}