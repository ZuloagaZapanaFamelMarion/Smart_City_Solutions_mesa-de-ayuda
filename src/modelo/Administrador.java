package modelo;

/**
 * Administrador del sistema.
 */
public class Administrador extends Usuario {
    public Administrador(int id, String nombre, String email, String password) {
        super(id, nombre, email, password, Rol.ADMINISTRADOR);
    }
}
