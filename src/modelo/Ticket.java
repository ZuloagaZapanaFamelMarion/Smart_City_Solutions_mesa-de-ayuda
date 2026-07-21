package modelo;

import interfaces.IEstadoTicket;
import interfaces.IObservador;
import interfaces.ISujeto;
import state.EstadoNuevo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Ticket base del Help Desk.
 * State: delega transiciones a IEstadoTicket.
 * Observer: actúa como sujeto al cambiar de estado.
 * GRASP Experto: conoce su propia información.
 */
public abstract class Ticket implements ISujeto {
    private final int id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private final Usuario solicitante;
    private Usuario tecnicoAsignado;
    private IEstadoTicket estado;
    private final LocalDateTime fechaCreacion;
    private final List<IObservador> observadores = new ArrayList<>();

    protected Ticket(int id, String titulo, String descripcion,
                     Prioridad prioridad, Usuario solicitante) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.solicitante = solicitante;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = new EstadoNuevo();
        if (solicitante != null) {
            agregarObservador(solicitante);
        }
    }

    public abstract TipoTicket getTipo();

    public abstract String getDetalleTipo();

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = prioridad;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public Usuario getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    public void setTecnicoAsignado(Usuario tecnicoAsignado) {
        this.tecnicoAsignado = tecnicoAsignado;
        if (tecnicoAsignado != null) {
            agregarObservador(tecnicoAsignado);
        }
    }

    public IEstadoTicket getEstado() {
        return estado;
    }

    public void setEstado(IEstadoTicket estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void avanzarEstado() {
        estado.siguiente(this);
    }

    public void cancelar() {
        estado.cancelar(this);
    }

    public void publicarCambioEstado(String detalle) {
        notificarObservadores("CAMBIO_ESTADO", detalle);
    }

    @Override
    public void agregarObservador(IObservador observador) {
        if (observador != null && !observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    @Override
    public void eliminarObservador(IObservador observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificarObservadores(String evento, String detalle) {
        for (IObservador observador : new ArrayList<>(observadores)) {
            observador.actualizar(evento, detalle);
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String tecnico = tecnicoAsignado == null ? "Sin asignar" : tecnicoAsignado.getNombre();
        return String.format(
                "#%d | %s | %s | %s | Prioridad: %s | Estado: %s | Solicitante: %s | Técnico: %s | %s",
                id, getTipo(), titulo, getDetalleTipo(), prioridad, estado.getNombre(),
                solicitante.getNombre(), tecnico, fechaCreacion.format(fmt)
        );
    }
}
