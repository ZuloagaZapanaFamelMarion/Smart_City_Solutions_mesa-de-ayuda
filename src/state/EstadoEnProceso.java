package state;

import interfaces.IEstadoTicket;
import modelo.Ticket;

/**
 * Ticket en atención.
 */
public class EstadoEnProceso implements IEstadoTicket {
    @Override
    public void siguiente(Ticket ticket) {
        ticket.setEstado(new EstadoResuelto());
        ticket.publicarCambioEstado("Ticket #" + ticket.getId() + " pasó a RESUELTO");
    }

    @Override
    public void cancelar(Ticket ticket) {
        ticket.setEstado(new EstadoCerrado());
        ticket.publicarCambioEstado("Ticket #" + ticket.getId() + " fue CANCELADO/CERRADO");
    }

    @Override
    public String getNombre() {
        return "EN_PROCESO";
    }
}
