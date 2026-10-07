import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { SectionRequest } from "../types";
import { updateSection } from "../service/section.service";

export function useUpdateSection(
  id: string | undefined,
  request: SectionRequest,
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
          title: "Sección no válida",
          description:
            "No se proporcionó un ID de sección válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar secciones",
          description:
            "Necesitas iniciar sesión para actualizar una sección.",
          type: "error",
        });
        return;
      }

      return updateSection(token, id, request);
    },
    onSuccess: async (section) => {
      toaster.create({
        title: "Sección actualizada",
        description: "La sección se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["sections"] });
      setIsOpen(false);
      resetForm();
      return section;
    },
    onError: (error) => {
      console.error("Error al actualizar sección:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar sección",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar sección",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar sección",
        description:
          "Ocurrió un error inesperado al actualizar la sección. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
