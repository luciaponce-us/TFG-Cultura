import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { isApiError } from "@/modules/core/utils/utils";
import { createSaga } from "../service/saga.service";

export function useCreateSaga() {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation<void, Error, string>({
    mutationFn: async (sagaName: string) => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear una saga",
          description: "Necesitas iniciar sesión para crear una nueva saga.",
          type: "error",
        });
        return;
      }

      await createSaga(token, sagaName);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Saga creada",
        description: "La saga se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["sagas"] });
    },
    onError: (error: unknown) => {
      if (isApiError(error)) {
        console.error("Error al crear saga:", error.message);
        toaster.create({
          title: "Error al crear saga",
          description: error.message,
          type: "error",
        });
      }
    },
  });
}
