package util;

import modelo.Prioridad;
import modelo.TipoTicket;

/**
 * Fabricación pura / SRP: validaciones centralizadas.
 */
public final class Validador {
    private Validador() {
    }

    public static void validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + campo + "' es obligatorio.");
        }
    }

    public static Prioridad parsePrioridad(String texto) {
        try {
            return Prioridad.valueOf(texto.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Prioridad inválida. Use: BAJA, MEDIA, ALTA, CRITICA");
        }
    }

    public static TipoTicket parseTipo(String texto) {
        try {
            return TipoTicket.valueOf(texto.trim().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Tipo inválido. Use: INCIDENTE, CONSULTA, MEJORA");
        }
    }

    public static int parseEntero(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("El campo '" + campo + "' debe ser numérico.");
        }
    }
}
