package facade;

import controlador.TicketService;
import interfaces.ITicketService;
import modelo.Prioridad;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;
import observer.HistorialNotificaciones;
import proxy.TicketServiceProxy;
import singleton.SesionUsuario;
import singleton.SistemaConfig;
import util.RepositorioDatos;

import java.util.List;
import java.util.Optional;

/**
 * Facade: simplifica el uso de varios subsistemas
 * (repositorio, servicio, proxy, sesión, configuración, historial).
 * Problema: la vista no debe conocer la complejidad interna.
 * Beneficio: una API simple para el menú / controlador de presentación.
 */
public class HelpDeskFacade {
    private final RepositorioDatos repositorio;
    private final TicketService ticketService;
    private final ITicketService ticketProxy;
    private final HistorialNotificaciones historial;
    private final SesionUsuario sesion;
    private final SistemaConfig config;

    public HelpDeskFacade() {
        this.repositorio = new RepositorioDatos();
        this.historial = new HistorialNotificaciones();
        this.ticketService = new TicketService(repositorio, historial);
        this.ticketProxy = new TicketServiceProxy(ticketService);
        this.sesion = SesionUsuario.getInstancia();
        this.config = SistemaConfig.getInstancia();
        this.config.setModoDebug(true);
    }

    public String getNombreSistema() {
        return config.toString();
    }

    public boolean login(String email, String password) {
        Optional<Usuario> opt = repositorio.buscarUsuarioPorEmail(email);
        if (opt.isEmpty() || !opt.get().validarPassword(password)) {
            return false;
        }
        sesion.iniciarSesion(opt.get());
        return true;
    }

    public void logout() {
        sesion.cerrarSesion();
    }

    public Usuario getUsuarioActual() {
        return sesion.getUsuarioActual();
    }

    public boolean haySesion() {
        return sesion.haySesionActiva();
    }

    public Ticket crearTicket(TipoTicket tipo, String titulo, String descripcion,
                              Prioridad prioridad, String datoExtra) {
        Usuario actual = exigirSesion();
        return ticketService.crearTicket(tipo, titulo, descripcion, prioridad, actual, datoExtra);
    }

    public List<Ticket> listarTickets() {
        return ticketProxy.listarTodos();
    }

    public Ticket buscarTicket(int id) {
        return ticketProxy.buscarPorId(id);
    }

    public boolean actualizarTicket(int id, String titulo, String descripcion, Prioridad prioridad) {
        exigirSesion();
        return ticketProxy.actualizarTicket(id, titulo, descripcion, prioridad);
    }

    public boolean eliminarTicket(int id) {
        Usuario actual = exigirSesion();
        return ticketProxy.eliminarTicket(id, actual);
    }

    public boolean asignarTecnico(int ticketId, int tecnicoId) {
        Usuario actual = exigirSesion();
        Optional<Usuario> tecnico = repositorio.buscarUsuarioPorId(tecnicoId);
        if (tecnico.isEmpty()) {
            return false;
        }
        return ticketProxy.asignarTecnico(ticketId, tecnico.get(), actual);
    }

    public boolean avanzarEstado(int id) {
        Usuario actual = exigirSesion();
        return ticketProxy.avanzarEstado(id, actual);
    }

    public boolean cancelarTicket(int id) {
        Usuario actual = exigirSesion();
        return ticketProxy.cancelarTicket(id, actual);
    }

    public List<Usuario> listarUsuarios() {
        return repositorio.getUsuarios();
    }

    public List<Usuario> listarTecnicos() {
        return repositorio.getTecnicos();
    }

    public void mostrarHistorial() {
        historial.mostrarHistorial();
    }

    public List<String> getHistorial() {
        return historial.getHistorial();
    }

    private Usuario exigirSesion() {
        if (!sesion.haySesionActiva()) {
            throw new IllegalStateException("Debe iniciar sesión primero.");
        }
        return sesion.getUsuarioActual();
    }
}
