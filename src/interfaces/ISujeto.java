package interfaces;

/**
 * Observer (lado sujeto / publisher).
 */
public interface ISujeto {
    void agregarObservador(IObservador observador);

    void eliminarObservador(IObservador observador);

    void notificarObservadores(String evento, String detalle);
}
