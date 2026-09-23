import type { Dispatch, SetStateAction } from "react";
import type { RolSagaErrors, RolSagaRequest } from "../types/rolgame";
import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { updateRolSaga } from "../service/rolsaga.service";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

export function useUpdateRolSaga(
  id: string,
  request: RolSagaRequest,
  image: File | null,
  setErrors: Dispatch<SetStateAction<RolSagaErrors>>,
  setIsOpen: Dispatch<SetStateAction<boolean>>,
  resetForm: () => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar sagas de juegos de rol",
          description:
            "Necesitas iniciar sesión para actualizar una saga de juegos de rol.",
          type: "error",
        });
        return;
      }

      return updateRolSaga(token, id, request, image);
    },
    onSuccess: async (rolSaga) => {
      toaster.create({
        title: "Saga de juegos de rol actualizada",
        description:
          "La saga de juegos de rol se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["rolsagas"] });
      setIsOpen(false);
      resetForm();
      return rolSaga;
    },
    onError: (error) => {
      console.error("Error al actualizar saga de juegos de rol:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar saga de juegos de rol",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar saga de juegos de rol",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar saga de juegos de rol",
        description:
          "Ocurrió un error inesperado al actualizar la saga de juegos de rol. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
