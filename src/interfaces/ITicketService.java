package interfaces;

import modelo.Prioridad;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;

import java.util.List;

/**
 * DIP: controladores y proxy dependen de esta abstracción.
 */
public interface ITicketService {
    Ticket crearTicket(TipoTicket tipo, String titulo, String descripcion,
                       Prioridad prioridad, Usuario solicitante);

    Ticket buscarPorId(int id);

    List<Ticket> listarTodos();

    boolean actualizarTicket(int id, String titulo, String descripcion, Prioridad prioridad);

    boolean eliminarTicket(int id, Usuario solicitante);

    boolean asignarTecnico(int id, Usuario tecnico, Usuario solicitante);

    boolean avanzarEstado(int id, Usuario solicitante);

    boolean cancelarTicket(int id, Usuario solicitante);
}
