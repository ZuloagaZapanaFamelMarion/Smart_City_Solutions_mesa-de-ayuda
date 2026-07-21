package util;

import modelo.Administrador;
import modelo.Cliente;
import modelo.Tecnico;
import modelo.Ticket;
import modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Persistencia básica en memoria (ArrayList).
 * GRASP Experto / SRP: solo gestiona almacenamiento.
 */
public class RepositorioDatos {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Ticket> tickets = new ArrayList<>();
    private final AtomicInteger secuenciaUsuario = new AtomicInteger(1);
    private final AtomicInteger secuenciaTicket = new AtomicInteger(1);

    public RepositorioDatos() {
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        usuarios.add(new Administrador(secuenciaUsuario.getAndIncrement(),
                "Admin Sistema", "admin@helpdesk.com", "admin123"));
        usuarios.add(new Tecnico(secuenciaUsuario.getAndIncrement(),
                "Ana Técnico", "ana@helpdesk.com", "tec123", "Redes"));
        usuarios.add(new Tecnico(secuenciaUsuario.getAndIncrement(),
                "Luis Técnico", "luis@helpdesk.com", "tec123", "Software"));
        usuarios.add(new Cliente(secuenciaUsuario.getAndIncrement(),
                "María Cliente", "maria@ciudad.com", "cli123"));
        usuarios.add(new Cliente(secuenciaUsuario.getAndIncrement(),
                "Pedro Cliente", "pedro@ciudad.com", "cli123"));
    }

    public int siguienteIdTicket() {
        return secuenciaTicket.getAndIncrement();
    }

    public int siguienteIdUsuario() {
        return secuenciaUsuario.getAndIncrement();
    }

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Optional<Usuario> buscarUsuarioPorId(int id) {
        return usuarios.stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<Ticket> buscarTicketPorId(int id) {
        return tickets.stream().filter(t -> t.getId() == id).findFirst();
    }

    public List<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets);
    }

    public boolean eliminarTicket(int id) {
        return tickets.removeIf(t -> t.getId() == id);
    }

    public List<Usuario> getTecnicos() {
        List<Usuario> tecnicos = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (u.getRol().name().equals("TECNICO")) {
                tecnicos.add(u);
            }
        }
        return tecnicos;
    }
}
