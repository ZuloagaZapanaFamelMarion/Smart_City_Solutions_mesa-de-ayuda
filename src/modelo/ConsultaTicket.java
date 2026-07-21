package modelo;

import java.time.LocalDateTime;

/**
 * Ticket de tipo consulta / pregunta.
 */
public class ConsultaTicket extends Ticket {
    private final String categoria;

    public ConsultaTicket(int id, String titulo, String descripcion,
                          Prioridad prioridad, Usuario solicitante, String categoria) {
        super(id, titulo, descripcion, prioridad, solicitante);
        this.categoria = categoria;
    }

    public ConsultaTicket(int id, String titulo, String descripcion,
                          Prioridad prioridad, Usuario solicitante, String categoria,
                          LocalDateTime fechaCreacion) {
        super(id, titulo, descripcion, prioridad, solicitante, fechaCreacion);
        this.categoria = categoria;
    }

    public String getCategoria() {
        return categoria;
    }

    @Override
    public TipoTicket getTipo() {
        return TipoTicket.CONSULTA;
    }

    @Override
    public String getDetalleTipo() {
        return "Categoría: " + categoria;
    }
}
