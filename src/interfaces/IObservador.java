package interfaces;

/**
 * Observer (lado observador).
 * DIP: el sujeto depende de esta abstracción, no de clases concretas.
 */
public interface IObservador {
    void actualizar(String evento, String detalle);
}
