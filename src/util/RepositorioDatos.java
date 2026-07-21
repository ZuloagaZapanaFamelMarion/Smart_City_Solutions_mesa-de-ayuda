package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import factory.TicketFactory;
import modelo.Administrador;
import modelo.Cliente;
import modelo.ConsultaTicket;
import modelo.IncidenteTicket;
import modelo.MejoraTicket;
import modelo.Prioridad;
import modelo.Rol;
import modelo.Tecnico;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;
import state.EstadoCerrado;
import state.EstadoEnProceso;
import state.EstadoNuevo;
import state.EstadoResuelto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repositorio con colecciones en memoria y persistencia intermedia en JSON.
 * GRASP Experto / SRP: solo gestiona almacenamiento y recuperación de datos.
 */
public class RepositorioDatos {
    private static final Path ARCHIVO_DATOS = Path.of("data", "helpdesk.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Ticket> tickets = new ArrayList<>();
    private final AtomicInteger secuenciaUsuario = new AtomicInteger(1);
    private final AtomicInteger secuenciaTicket = new AtomicInteger(1);

    public RepositorioDatos() {
        if (Files.exists(ARCHIVO_DATOS)) {
            cargarDesdeJson();
        } else {
            cargarDatosIniciales();
            guardarDatos();
        }
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
        guardarDatos();
    }

    public void agregarTicket(Ticket ticket) {
        tickets.add(ticket);
        guardarDatos();
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
        boolean eliminado = tickets.removeIf(t -> t.getId() == id);
        if (eliminado) {
            guardarDatos();
        }
        return eliminado;
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

    public synchronized void guardarDatos() {
        BaseDatosJson datos = new BaseDatosJson();
        for (Usuario usuario : usuarios) {
            UsuarioJson item = new UsuarioJson();
            item.id = usuario.getId();
            item.nombre = usuario.getNombre();
            item.email = usuario.getEmail();
            item.password = usuario.getPassword();
            item.rol = usuario.getRol().name();
            item.especialidad = usuario instanceof Tecnico tecnico
                    ? tecnico.getEspecialidad() : null;
            datos.usuarios.add(item);
        }

        for (Ticket ticket : tickets) {
            TicketJson item = new TicketJson();
            item.id = ticket.getId();
            item.tipo = ticket.getTipo().name();
            item.titulo = ticket.getTitulo();
            item.descripcion = ticket.getDescripcion();
            item.prioridad = ticket.getPrioridad().name();
            item.solicitanteId = ticket.getSolicitante().getId();
            item.tecnicoId = ticket.getTecnicoAsignado() == null
                    ? null : ticket.getTecnicoAsignado().getId();
            item.estado = ticket.getEstado().getNombre();
            item.fechaCreacion = ticket.getFechaCreacion().toString();
            item.datoExtra = obtenerDatoExtra(ticket);
            datos.tickets.add(item);
        }

        try {
            Files.createDirectories(ARCHIVO_DATOS.getParent());
            Path temporal = ARCHIVO_DATOS.resolveSibling(ARCHIVO_DATOS.getFileName() + ".tmp");
            Files.writeString(temporal, GSON.toJson(datos), StandardCharsets.UTF_8);
            Files.move(temporal, ARCHIVO_DATOS,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar data/helpdesk.json", e);
        }
    }

    private void cargarDesdeJson() {
        try {
            String contenido = Files.readString(ARCHIVO_DATOS, StandardCharsets.UTF_8);
            BaseDatosJson datos = GSON.fromJson(contenido, BaseDatosJson.class);
            if (datos == null || datos.usuarios == null || datos.tickets == null) {
                throw new IllegalStateException("El archivo JSON no tiene la estructura esperada.");
            }

            for (UsuarioJson item : datos.usuarios) {
                usuarios.add(crearUsuario(item));
            }
            for (TicketJson item : datos.tickets) {
                usuarios.stream()
                        .filter(u -> u.getId() == item.solicitanteId)
                        .findFirst()
                        .ifPresent(solicitante -> tickets.add(crearTicket(item, solicitante)));
            }

            int maxUsuario = usuarios.stream()
                    .map(Usuario::getId).max(Comparator.naturalOrder()).orElse(0);
            int maxTicket = tickets.stream()
                    .map(Ticket::getId).max(Comparator.naturalOrder()).orElse(0);
            secuenciaUsuario.set(maxUsuario + 1);
            secuenciaTicket.set(maxTicket + 1);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException(
                    "No se pudo cargar data/helpdesk.json. Revise que sea un JSON válido.", e);
        }
    }

    private Usuario crearUsuario(UsuarioJson item) {
        Rol rol = Rol.valueOf(item.rol);
        return switch (rol) {
            case ADMINISTRADOR ->
                    new Administrador(item.id, item.nombre, item.email, item.password);
            case TECNICO ->
                    new Tecnico(item.id, item.nombre, item.email, item.password, item.especialidad);
            case CLIENTE ->
                    new Cliente(item.id, item.nombre, item.email, item.password);
        };
    }

    private Ticket crearTicket(TicketJson item, Usuario solicitante) {
        Ticket ticket = TicketFactory.restaurar(
                TipoTicket.valueOf(item.tipo),
                item.id,
                item.titulo,
                item.descripcion,
                Prioridad.valueOf(item.prioridad),
                solicitante,
                item.datoExtra,
                LocalDateTime.parse(item.fechaCreacion)
        );

        if (item.tecnicoId != null) {
            buscarUsuarioPorId(item.tecnicoId).ifPresent(ticket::setTecnicoAsignado);
        }
        ticket.setEstado(switch (item.estado) {
            case "EN_PROCESO" -> new EstadoEnProceso();
            case "RESUELTO" -> new EstadoResuelto();
            case "CERRADO" -> new EstadoCerrado();
            default -> new EstadoNuevo();
        });
        return ticket;
    }

    private String obtenerDatoExtra(Ticket ticket) {
        if (ticket instanceof IncidenteTicket incidente) {
            return incidente.getSistemaAfectado();
        }
        if (ticket instanceof ConsultaTicket consulta) {
            return consulta.getCategoria();
        }
        return ((MejoraTicket) ticket).getBeneficioEsperado();
    }

    private static class BaseDatosJson {
        private List<UsuarioJson> usuarios = new ArrayList<>();
        private List<TicketJson> tickets = new ArrayList<>();
    }

    private static class UsuarioJson {
        private int id;
        private String nombre;
        private String email;
        private String password;
        private String rol;
        private String especialidad;
    }

    private static class TicketJson {
        private int id;
        private String tipo;
        private String titulo;
        private String descripcion;
        private String prioridad;
        private int solicitanteId;
        private Integer tecnicoId;
        private String estado;
        private String fechaCreacion;
        private String datoExtra;
    }
}
