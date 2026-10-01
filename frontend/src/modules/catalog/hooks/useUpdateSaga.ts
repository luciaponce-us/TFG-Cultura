import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import { updateSaga } from "../service/saga.service";
import type { Dispatch, SetStateAction } from "react";

export function useUpdateSaga(
  id: string | undefined,
  name: string,
  setError: Dispatch<SetStateAction<string | null>>,
  setIsOpen: (isOpen: boolean) => void,
  resetForm: () => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!id) {
        toaster.create({
          title: "Saga no válida",
          description:
            "No se proporcionó un ID de saga válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar sagas",
          description: "Necesitas iniciar sesión para actualizar una saga.",
          type: "error",
        });
        return;
      }

      return updateSaga(token, id, name);
    },
    onSuccess: async (saga) => {
      toaster.create({
        title: "Saga actualizada",
        description: "La saga se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["sagas"] });
      await queryClient.invalidateQueries({
        queryKey: ["books"],
      });
      await queryClient.invalidateQueries({
        queryKey: ["movies"],
      });
      setIsOpen(false);
      resetForm();
      return saga;
    },
    onError: (error) => {
      console.error("Error al actualizar saga:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setError(error.errors.name);
          toaster.create({
            title: "Error al actualizar saga",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar saga",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar saga",
        description:
          "Ocurrió un error inesperado al actualizar la saga. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
