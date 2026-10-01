import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { CategoryCreateRequest } from "../types";
import { updateCategory } from "../service/categories.service";

export function useUpdateCategory(
  id: string | undefined,
  request: CategoryCreateRequest,
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
          title: "Categoría no válida",
          description:
            "No se proporcionó un ID de categoría válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar categorías",
          description:
            "Necesitas iniciar sesión para actualizar una categoría.",
          type: "error",
        });
        return;
      }

      return updateCategory(id, request, token);
    },
    onSuccess: async (category) => {
      toaster.create({
        title: "Categoría actualizada",
        description: "La categoría se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["categories"] });
      setIsOpen(false);
      resetForm();
      return category;
    },
    onError: (error) => {
      console.error("Error al actualizar categoría:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar categoría",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar categoría",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar categoría",
        description:
          "Ocurrió un error inesperado al actualizar la categoría. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
