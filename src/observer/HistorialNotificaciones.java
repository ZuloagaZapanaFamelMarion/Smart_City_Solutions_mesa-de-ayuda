package observer;

import interfaces.IObservador;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer auxiliar: historial centralizado de notificaciones del sistema.
 * Complementa las notificaciones que reciben los usuarios del ticket.
 */
public class HistorialNotificaciones implements IObservador {
    private final List<String> historial = new ArrayList<>();

    @Override
    public void actualizar(String evento, String detalle) {
        String registro = "[" + evento + "] " + detalle;
        historial.add(registro);
        System.out.println("  [Historial] " + registro);
    }

    public List<String> getHistorial() {
        return new ArrayList<>(historial);
    }

    public void mostrarHistorial() {
        if (historial.isEmpty()) {
            System.out.println("  No hay notificaciones registradas.");
            return;
        }
        System.out.println("=== Historial de notificaciones ===");
        for (String item : historial) {
            System.out.println("  - " + item);
        }
    }
}
