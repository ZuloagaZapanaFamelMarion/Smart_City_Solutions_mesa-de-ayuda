package controlador;

import factory.TicketFactory;
import interfaces.ITicketService;
import modelo.Prioridad;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;
import observer.HistorialNotificaciones;
import singleton.SistemaConfig;
import util.RepositorioDatos;
import util.Validador;

import java.util.List;
import java.util.Optional;

/**
 * Servicio real de tickets (sujeto del Proxy).
 * SRP: lógica de negocio de tickets.
 * GRASP Controlador / Creador.
 */
public class TicketService implements ITicketService {
    private final RepositorioDatos repositorio;
    private final HistorialNotificaciones historial;

    public TicketService(RepositorioDatos repositorio, HistorialNotificaciones historial) {
        this.repositorio = repositorio;
        this.historial = historial;
        this.repositorio.getTickets().forEach(ticket -> ticket.agregarObservador(historial));
    }

    @Override
    public Ticket crearTicket(TipoTicket tipo, String titulo, String descripcion,
                              Prioridad prioridad, Usuario solicitante) {
        return crearTicket(tipo, titulo, descripcion, prioridad, solicitante, null);
    }

    public Ticket crearTicket(TipoTicket tipo, String titulo, String descripcion,
                              Prioridad prioridad, Usuario solicitante, String datoExtra) {
        Validador.validarTexto(titulo, "título");
        Validador.validarTexto(descripcion, "descripción");
        if (solicitante == null) {
            throw new IllegalArgumentException("Debe existir un solicitante.");
        }

        int id = repositorio.siguienteIdTicket();
        Ticket ticket = TicketFactory.crear(tipo, id, titulo, descripcion, prioridad, solicitante, datoExtra);
        ticket.agregarObservador(historial);
        repositorio.agregarTicket(ticket);
        SistemaConfig.getInstancia().log("Ticket creado: #" + id);
        historial.actualizar("CREACION", "Se creó el ticket #" + id + " (" + tipo + ")");
        return ticket;
    }

    @Override
    public Ticket buscarPorId(int id) {
        return repositorio.buscarTicketPorId(id).orElse(null);
    }

    @Override
    public List<Ticket> listarTodos() {
        return repositorio.getTickets();
    }

    @Override
    public boolean actualizarTicket(int id, String titulo, String descripcion, Prioridad prioridad) {
        Optional<Ticket> opt = repositorio.buscarTicketPorId(id);
        if (opt.isEmpty()) {
            return false;
        }
        Ticket ticket = opt.get();
        if (titulo != null && !titulo.isBlank()) {
            ticket.setTitulo(titulo.trim());
        }
        if (descripcion != null && !descripcion.isBlank()) {
            ticket.setDescripcion(descripcion.trim());
        }
        if (prioridad != null) {
            ticket.setPrioridad(prioridad);
        }
        repositorio.guardarDatos();
        historial.actualizar("ACTUALIZACION", "Ticket #" + id + " actualizado");
        return true;
    }

    @Override
    public boolean eliminarTicket(int id, Usuario solicitante) {
        boolean eliminado = repositorio.eliminarTicket(id);
        if (eliminado) {
            historial.actualizar("ELIMINACION", "Ticket #" + id + " eliminado por " + solicitante.getNombre());
        }
        return eliminado;
    }

    @Override
    public boolean asignarTecnico(int id, Usuario tecnico, Usuario solicitante) {
        Optional<Ticket> opt = repositorio.buscarTicketPorId(id);
        if (opt.isEmpty() || tecnico == null) {
            return false;
        }
        Ticket ticket = opt.get();
        ticket.setTecnicoAsignado(tecnico);
        repositorio.guardarDatos();
        ticket.notificarObservadores("ASIGNACION",
                "Ticket #" + id + " asignado a " + tecnico.getNombre());
        return true;
    }

    @Override
    public boolean avanzarEstado(int id, Usuario solicitante) {
        Optional<Ticket> opt = repositorio.buscarTicketPorId(id);
        if (opt.isEmpty()) {
            return false;
        }
        opt.get().avanzarEstado();
        repositorio.guardarDatos();
        return true;
    }

    @Override
    public boolean cancelarTicket(int id, Usuario solicitante) {
        Optional<Ticket> opt = repositorio.buscarTicketPorId(id);
        if (opt.isEmpty()) {
            return false;
        }
        opt.get().cancelar();
        repositorio.guardarDatos();
        return true;
    }
}
