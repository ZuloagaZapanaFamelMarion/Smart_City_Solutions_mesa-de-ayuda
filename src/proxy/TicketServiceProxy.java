package proxy;

import interfaces.ITicketService;
import modelo.Prioridad;
import modelo.Rol;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;
import singleton.SistemaConfig;

import java.util.List;

/**
 * Proxy: control de acceso sobre operaciones sensibles.
 * Problema: clientes no deben eliminar ni avanzar estados críticos.
 * Beneficio: seguridad y auditoría sin modificar el servicio real.
 */
public class TicketServiceProxy implements ITicketService {
    private final ITicketService servicioReal;

    public TicketServiceProxy(ITicketService servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public Ticket crearTicket(TipoTicket tipo, String titulo, String descripcion,
                              Prioridad prioridad, Usuario solicitante) {
        return servicioReal.crearTicket(tipo, titulo, descripcion, prioridad, solicitante);
    }

    @Override
    public Ticket buscarPorId(int id) {
        return servicioReal.buscarPorId(id);
    }

    @Override
    public List<Ticket> listarTodos() {
        return servicioReal.listarTodos();
    }

    @Override
    public boolean actualizarTicket(int id, String titulo, String descripcion, Prioridad prioridad) {
        return servicioReal.actualizarTicket(id, titulo, descripcion, prioridad);
    }

    @Override
    public boolean eliminarTicket(int id, Usuario solicitante) {
        if (!esAdminOTecnico(solicitante)) {
            System.out.println("  Acceso denegado: solo TÉCNICO o ADMINISTRADOR puede eliminar.");
            return false;
        }
        SistemaConfig.getInstancia().log("Proxy autorizó eliminación por " + solicitante.getNombre());
        return servicioReal.eliminarTicket(id, solicitante);
    }

    @Override
    public boolean asignarTecnico(int id, Usuario tecnico, Usuario solicitante) {
        if (!esAdminOTecnico(solicitante)) {
            System.out.println("  Acceso denegado: solo TÉCNICO o ADMINISTRADOR puede asignar.");
            return false;
        }
        return servicioReal.asignarTecnico(id, tecnico, solicitante);
    }

    @Override
    public boolean avanzarEstado(int id, Usuario solicitante) {
        if (!esAdminOTecnico(solicitante)) {
            System.out.println("  Acceso denegado: solo TÉCNICO o ADMINISTRADOR puede cambiar estado.");
            return false;
        }
        return servicioReal.avanzarEstado(id, solicitante);
    }

    @Override
    public boolean cancelarTicket(int id, Usuario solicitante) {
        if (solicitante.getRol() != Rol.ADMINISTRADOR
                && solicitante.getRol() != Rol.TECNICO
                && solicitante.getRol() != Rol.CLIENTE) {
            System.out.println("  Acceso denegado.");
            return false;
        }
        // Cliente solo cancela sus propios tickets
        if (solicitante.getRol() == Rol.CLIENTE) {
            Ticket ticket = servicioReal.buscarPorId(id);
            if (ticket == null || ticket.getSolicitante().getId() != solicitante.getId()) {
                System.out.println("  Acceso denegado: solo puede cancelar sus propios tickets.");
                return false;
            }
        }
        return servicioReal.cancelarTicket(id, solicitante);
    }

    private boolean esAdminOTecnico(Usuario usuario) {
        return usuario != null
                && (usuario.getRol() == Rol.ADMINISTRADOR || usuario.getRol() == Rol.TECNICO);
    }
}
