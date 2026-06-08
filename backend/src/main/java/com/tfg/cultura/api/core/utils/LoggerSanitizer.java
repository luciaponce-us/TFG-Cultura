package com.tfg.cultura.api.core.utils;

public class LoggerSanitizer {

    /**
     * Sanitiza una cadena para evitar inyección de logs y otros problemas relacionados con caracteres de control.
     * @param input
     * @return cadena sanitizada, con caracteres de control eliminados y tamaño limitado a 500 caracteres.
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        // Reemplaza caracteres de nueva línea, retorno de carro y tabulación por guiones bajos
        return input
            .replaceAll("[\r\n\t]", " ")   // evita log injection (CRLF)
            .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "") // elimina control chars
            .trim()
            .substring(0, Math.min(input.length(), 500)); // límite de tamaño
    }
    
}
