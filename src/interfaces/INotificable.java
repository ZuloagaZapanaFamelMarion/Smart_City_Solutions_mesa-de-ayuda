package interfaces;

/**
 * ISP: interfaz pequeña solo para quien puede recibir notificaciones.
 */
public interface INotificable {
    void recibirNotificacion(String mensaje);
}
