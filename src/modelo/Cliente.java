package modelo;

/**
 * Cliente que reporta tickets.
 */
public class Cliente extends Usuario {
    public Cliente(int id, String nombre, String email, String password) {
        super(id, nombre, email, password, Rol.CLIENTE);
    }
}
