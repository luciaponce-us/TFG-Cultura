import { useAuth } from "@/modules/core/context/useAuth";
import type { RolGameRequest } from "../types/rolgame";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createRolGame } from "../service/rolgame.service";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

export function useCreateRolGame(
  request: RolGameRequest,
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
          title: "Inicia sesión para crear juegos de rol",
          description:
            "Necesitas iniciar sesión para crear un nuevo juego de rol.",
          type: "error",
        });
        return;
      }

      return createRolGame(token, request, image);
    },
    onSuccess: async (rolGame) => {
      toaster.create({
        title: "Juego de rol creado",
        description: "El juego de rol se ha creado correctamente.",
        type: "success",
      });

      await queryClient.invalidateQueries({ queryKey: ["rolgames"] });
      setIsOpen(false);
      return rolGame;
    },
    onError: (error: Error) => {
      console.error("Error al crear juego de rol:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear juego de rol",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear juego de rol",
          description: error.message,
          type: "error",
        });
      }
    },
  });
}
