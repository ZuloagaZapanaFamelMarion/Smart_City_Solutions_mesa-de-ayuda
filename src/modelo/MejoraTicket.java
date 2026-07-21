package modelo;

import java.time.LocalDateTime;

/**
 * Ticket de tipo solicitud de mejora.
 */
public class MejoraTicket extends Ticket {
    private final String beneficioEsperado;

    public MejoraTicket(int id, String titulo, String descripcion,
                        Prioridad prioridad, Usuario solicitante, String beneficioEsperado) {
        super(id, titulo, descripcion, prioridad, solicitante);
        this.beneficioEsperado = beneficioEsperado;
    }

    public MejoraTicket(int id, String titulo, String descripcion,
                        Prioridad prioridad, Usuario solicitante, String beneficioEsperado,
                        LocalDateTime fechaCreacion) {
        super(id, titulo, descripcion, prioridad, solicitante, fechaCreacion);
        this.beneficioEsperado = beneficioEsperado;
    }

    public String getBeneficioEsperado() {
        return beneficioEsperado;
    }

    @Override
    public TipoTicket getTipo() {
        return TipoTicket.MEJORA;
    }

    @Override
    public String getDetalleTipo() {
        return "Beneficio: " + beneficioEsperado;
    }
}
