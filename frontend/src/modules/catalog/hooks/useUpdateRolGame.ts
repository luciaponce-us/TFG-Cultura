import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { RolGameRequest } from "../types/rolgame";
import { updateRolGame } from "../service/rolgame.service";

export function useUpdateRolGame(
  id: string | undefined,
  request: RolGameRequest,
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
          title: "Juego de rol no válido",
          description:
            "No se proporcionó un ID de juego de rol válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar juegos de rol",
          description:
            "Necesitas iniciar sesión para actualizar un juego de rol.",
          type: "error",
        });
        return;
      }

      return updateRolGame(token, id, request, image);
    },
    onSuccess: async (rolGame) => {
      toaster.create({
        title: "Juego de rol actualizado",
        description: "El juego de rol se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["rolgames"] });
      setIsOpen(false);
      resetForm();
      return rolGame;
    },
    onError: (error) => {
      console.error("Error al actualizar juego de rol:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar juego de rol",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar juego de rol",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar juego de rol",
        description:
          "Ocurrió un error inesperado al actualizar el juego de rol. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
