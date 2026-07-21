package modelo;

import interfaces.INotificable;
import interfaces.IObservador;

/**
 * Clase base de usuarios.
 * LSP: Cliente, Tecnico y Administrador pueden sustituir a Usuario.
 * GRASP Experto: conoce sus propios datos.
 */
public abstract class Usuario implements INotificable, IObservador {
    private final int id;
    private String nombre;
    private String email;
    private String password;
    private final Rol rol;

    protected Usuario(int id, String nombre, String email, String password, Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public boolean validarPassword(String clave) {
        return password != null && password.equals(clave);
    }

    @Override
    public void recibirNotificacion(String mensaje) {
        System.out.println("  [Notificación → " + nombre + "] " + mensaje);
    }

    @Override
    public void actualizar(String evento, String detalle) {
        recibirNotificacion(evento + ": " + detalle);
    }

    @Override
    public String toString() {
        return id + " | " + nombre + " | " + email + " | " + rol;
    }
}
