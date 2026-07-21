package singleton;

/**
 * Singleton: configuración global del sistema.
 * Problema: evitar múltiples instancias de configuración inconsistentes.
 * Beneficio: un único punto de acceso a parámetros globales.
 */
public class SistemaConfig {
    private static SistemaConfig instancia;

    private final String nombreSistema;
    private final String version;
    private boolean modoDebug;

    private SistemaConfig() {
        this.nombreSistema = "SmartCity Help Desk";
        this.version = "1.0.0";
        this.modoDebug = false;
    }

    public static synchronized SistemaConfig getInstancia() {
        if (instancia == null) {
            instancia = new SistemaConfig();
        }
        return instancia;
    }

    public String getNombreSistema() {
        return nombreSistema;
    }

    public String getVersion() {
        return version;
    }

    public boolean isModoDebug() {
        return modoDebug;
    }

    public void setModoDebug(boolean modoDebug) {
        this.modoDebug = modoDebug;
    }

    public void log(String mensaje) {
        if (modoDebug) {
            System.out.println("[DEBUG] " + mensaje);
        }
    }

    @Override
    public String toString() {
        return nombreSistema + " v" + version;
    }
}
