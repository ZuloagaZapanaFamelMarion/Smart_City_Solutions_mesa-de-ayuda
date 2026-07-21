package state;

import interfaces.IEstadoTicket;
import modelo.Ticket;

/**
 * Ticket resuelto, pendiente de cierre.
 */
public class EstadoResuelto implements IEstadoTicket {
    @Override
    public void siguiente(Ticket ticket) {
        ticket.setEstado(new EstadoCerrado());
        ticket.publicarCambioEstado("Ticket #" + ticket.getId() + " pasó a CERRADO");
    }

    @Override
    public void cancelar(Ticket ticket) {
        System.out.println("  No se puede cancelar un ticket ya resuelto. Use avanzar para cerrarlo.");
    }

    @Override
    public String getNombre() {
        return "RESUELTO";
    }
}
