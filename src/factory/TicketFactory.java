package factory;

import modelo.ConsultaTicket;
import modelo.IncidenteTicket;
import modelo.MejoraTicket;
import modelo.Prioridad;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;

/**
 * Factory Method / Simple Factory (Fabricación pura GRASP).
 * Problema: centralizar la creación de distintos tipos de ticket.
 * Beneficio: el cliente no conoce las clases concretas (OCP / DIP).
 */
public class TicketFactory {
    private TicketFactory() {
    }

    public static Ticket crear(TipoTicket tipo, int id, String titulo, String descripcion,
                               Prioridad prioridad, Usuario solicitante, String datoExtra) {
        return switch (tipo) {
            case INCIDENTE -> new IncidenteTicket(
                    id, titulo, descripcion, prioridad, solicitante,
                    datoExtra == null || datoExtra.isBlank() ? "General" : datoExtra
            );
            case CONSULTA -> new ConsultaTicket(
                    id, titulo, descripcion, prioridad, solicitante,
                    datoExtra == null || datoExtra.isBlank() ? "General" : datoExtra
            );
            case MEJORA -> new MejoraTicket(
                    id, titulo, descripcion, prioridad, solicitante,
                    datoExtra == null || datoExtra.isBlank() ? "Mejora operativa" : datoExtra
            );
        };
    }
}
