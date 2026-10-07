import { useMutation, useQueryClient } from "@tanstack/react-query";

import { addManagerToSection } from "../service/section.service";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import { toaster } from "@/modules/core/components/toaster/toaster";

interface AddManagerToSectionParams {
  token: string;
  sectionId: string;
  managerUsername: string;
}

export function useAddManagerToSection(setError: (errors: string) => void) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      token,
      sectionId,
      managerUsername,
    }: AddManagerToSectionParams) =>
      addManagerToSection(token, sectionId, managerUsername),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["sections"],
      });
    },
    onError: (error) => {
      console.error("Error al agregar encargado a la sección:", error);
      if (isApiError(error)) {
        if (isFieldError(error)) {
          if (error.errors.managers) {
            setError(
              "El encargado seleccionado ya es encargado de otra sección. Por favor, selecciona otro encargado.",
            );

            toaster.create({
              title: "Error al agregar encargado",
              description:
                "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
              type: "error",
            });

            return;
          }
        }

        toaster.create({
          title: "Error al agregar encargado",
          description: error.message,
          type: "error",
        });
        return;
      }
      
      toaster.create({
        title: "Error al agregar encargado",
        description:
          "Ocurrió un error al agregar el encargado. Inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
