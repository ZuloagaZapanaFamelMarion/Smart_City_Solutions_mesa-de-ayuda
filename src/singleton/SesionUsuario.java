package singleton;

import modelo.Usuario;

/**
 * Singleton: sesión del usuario autenticado.
 * Problema: compartir el usuario actual en toda la aplicación.
 * Beneficio: acceso global controlado a la sesión activa.
 */
public class SesionUsuario {
    private static SesionUsuario instancia;
    private Usuario usuarioActual;

    private SesionUsuario() {
    }

    public static synchronized SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    public void iniciarSesion(Usuario usuario) {
        this.usuarioActual = usuario;
    }

    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }
}
