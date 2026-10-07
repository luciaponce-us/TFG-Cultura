import { useMutation, useQueryClient } from "@tanstack/react-query";

import { addCollaboratorToSection } from "../service/section.service";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

interface AddCollaboratorToSectionParams {
  token: string;
  sectionId: string;
  collaboratorUsername: string;
}

export function useAddCollaboratorToSection(setError: (error: string) => void) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      token,
      sectionId,
      collaboratorUsername,
    }: AddCollaboratorToSectionParams) =>
      addCollaboratorToSection(token, sectionId, collaboratorUsername),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["sections"],
      });
    },
    onError: (error) => {
          console.error("Error al agregar colaborador a la sección:", error);
          if (isApiError(error)) {
            if (isFieldError(error)) {
              if (error.errors.collaborators) {
                setError(
                  "El colaborador seleccionado ya es colaborador de otra sección. Por favor, selecciona otro colaborador.",
                );
    
                toaster.create({
                  title: "Error al agregar colaborador",
                  description:
                    "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
                  type: "error",
                });
    
                return;
              }
            }
    
            toaster.create({
              title: "Error al agregar colaborador",
              description: error.message,
              type: "error",
            });
            return;
          }
          
          toaster.create({
            title: "Error al agregar colaborador",
            description:
              "Ocurrió un error al agregar el colaborador. Inténtalo de nuevo más tarde.",
            type: "error",
          });
        },
  });
}
