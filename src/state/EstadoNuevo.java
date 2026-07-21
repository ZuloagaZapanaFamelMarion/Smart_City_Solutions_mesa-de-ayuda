package state;

import interfaces.IEstadoTicket;
import modelo.Ticket;

/**
 * Estado inicial del ticket.
 */
public class EstadoNuevo implements IEstadoTicket {
    @Override
    public void siguiente(Ticket ticket) {
        ticket.setEstado(new EstadoEnProceso());
        ticket.publicarCambioEstado("Ticket #" + ticket.getId() + " pasó a EN PROCESO");
    }

    @Override
    public void cancelar(Ticket ticket) {
        ticket.setEstado(new EstadoCerrado());
        ticket.publicarCambioEstado("Ticket #" + ticket.getId() + " fue CANCELADO/CERRADO");
    }

    @Override
    public String getNombre() {
        return "NUEVO";
    }
}
