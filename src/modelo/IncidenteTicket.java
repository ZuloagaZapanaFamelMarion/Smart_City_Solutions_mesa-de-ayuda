package modelo;

/**
 * Ticket de tipo incidente (falla / error).
 */
public class IncidenteTicket extends Ticket {
    private final String sistemaAfectado;

    public IncidenteTicket(int id, String titulo, String descripcion,
                           Prioridad prioridad, Usuario solicitante, String sistemaAfectado) {
        super(id, titulo, descripcion, prioridad, solicitante);
        this.sistemaAfectado = sistemaAfectado;
    }

    public String getSistemaAfectado() {
        return sistemaAfectado;
    }

    @Override
    public TipoTicket getTipo() {
        return TipoTicket.INCIDENTE;
    }

    @Override
    public String getDetalleTipo() {
        return "Sistema: " + sistemaAfectado;
    }
}
