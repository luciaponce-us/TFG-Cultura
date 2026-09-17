import { toaster } from "@/modules/core/components";
import type { Dispatch, SetStateAction } from "react";

export const MAX_LENGTH = {
  NAME: 50
};

export function validateSagaName(name: string, setError: Dispatch<SetStateAction<string | null>>) : void{
    let errorMessage = null;
  if (!name || name.trim() === "") 
    errorMessage = "El nombre de la saga no puede estar vacío.";
    
  if (name.length < 3 || name.length > MAX_LENGTH.NAME)
    errorMessage = `El nombre de la saga debe tener entre 3 y ${MAX_LENGTH.NAME} caracteres.`;
  
  if (errorMessage) {
    setError(errorMessage);
    toaster.create({
      title: "Error al crear saga",
      description: "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
      type: "error",
    });
  }

}