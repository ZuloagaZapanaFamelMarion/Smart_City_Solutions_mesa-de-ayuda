package state;

import interfaces.IEstadoTicket;
import modelo.Ticket;

/**
 * Estado final: no admite más transiciones.
 */
public class EstadoCerrado implements IEstadoTicket {
    @Override
    public void siguiente(Ticket ticket) {
        System.out.println("  El ticket #" + ticket.getId() + " ya está CERRADO.");
    }

    @Override
    public void cancelar(Ticket ticket) {
        System.out.println("  El ticket #" + ticket.getId() + " ya está CERRADO.");
    }

    @Override
    public String getNombre() {
        return "CERRADO";
    }
}
