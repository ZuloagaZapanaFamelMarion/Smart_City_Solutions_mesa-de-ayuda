package interfaces;

import modelo.Ticket;

/**
 * State: contrato de cada estado del ciclo de vida del ticket.
 * OCP: se pueden agregar estados nuevos sin modificar los existentes.
 */
public interface IEstadoTicket {
    void siguiente(Ticket ticket);

    void cancelar(Ticket ticket);

    String getNombre();
}
