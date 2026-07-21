package modelo;

/**
 * Técnico que atiende tickets.
 */
public class Tecnico extends Usuario {
    private String especialidad;

    public Tecnico(int id, String nombre, String email, String password, String especialidad) {
        super(id, nombre, email, password, Rol.TECNICO);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return super.toString() + " | Esp: " + especialidad;
    }
}
