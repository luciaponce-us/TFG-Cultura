import { useAuth } from "@/modules/core/context/useAuth";
import type { RolSagaErrors, RolSagaRequest } from "../types/rolgame";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { createRolSaga } from "../service/rolsaga.service";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

export function useCreateRolSaga(
  request: RolSagaRequest,
  image: File | null,
  setErrors: (errors: RolSagaErrors) => void,
  setIsOpen: (isOpen: boolean) => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear sagas de rol",
          description:
            "Necesitas iniciar sesión para crear una nueva saga de rol.",
          type: "error",
        });
        return;
      }

      await createRolSaga(request, token, image);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Saga de rol creada",
        description: "La saga de rol se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["rolsagas"] });
      setIsOpen(false);
    },
    onError: (error: Error) => {
      console.error("Error al crear saga de rol:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear saga de rol",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear saga de rol",
          description: error.message,
          type: "error",
        });
        return;
      }
    },
  });
}
