import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { CategoryCreateRequest, CategoryFormErrors } from "../types";
import { createCategory } from "../service/categories.service";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { Dispatch, SetStateAction } from "react";

export function useCreateCategory(
  setErrors: Dispatch<SetStateAction<CategoryFormErrors>>,
  setIsOpen: (isOpen: boolean) => void,
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (category: CategoryCreateRequest) => {
      const token = localStorage.getItem("token");
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear categorías",
          description:
            "Necesitas iniciar sesión para crear una nueva categoría.",
          type: "error",
        });
        return;
      }

      return createCategory(category, token);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Categoría creada",
        description: "La categoría se ha creado correctamente.",
        type: "success",
      });
      await queryClient.invalidateQueries({ queryKey: ["categories"] });
      setIsOpen(false);
    },
    onError: (error) => {
      console.error("Error creando categoría:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear categoría",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear categoría",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al crear categoría",
        description:
          "Ocurrió un error inesperado al crear la categoría. Por favor, inténtalo de nuevo.",
        type: "error",
      });
    },
  });
}
