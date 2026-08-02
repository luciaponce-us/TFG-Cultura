package com.tfg.cultura.api.core.utils;

public class LoggerSanitizer {

    /**
     * Sanitiza una cadena para evitar inyección de logs y otros problemas
     * relacionados con caracteres de control.
     * 
     * @param input
     * @return cadena sanitizada, con caracteres de control eliminados y tamaño
     *         limitado a 500 caracteres.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Reemplaza caracteres de nueva línea, retorno de carro y tabulación por
        // guiones bajos
        String sanitized = input
                .replaceAll("[\r\n\t]", " ") // Reemplaza saltos de línea, retornos de carro y tabulaciones por espacios
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "") // Elimina otros caracteres de control
                .trim();

        return sanitized.substring(0, Math.min(sanitized.length(), 500)); // Limita la longitud a 500 caracteres
    }

}
