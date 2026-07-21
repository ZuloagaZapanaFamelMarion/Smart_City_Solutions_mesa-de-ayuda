package vista;

import facade.HelpDeskFacade;
import modelo.Prioridad;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;
import util.Validador;

import java.util.List;
import java.util.Scanner;

/**
 * Vista por consola (MVC).
 * SRP: solo interacción con el usuario; delega en el Facade.
 */
public class MenuPrincipal {
    private final HelpDeskFacade facade;
    private final Scanner scanner;

    public MenuPrincipal(HelpDeskFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            if (!facade.haySesion()) {
                salir = !mostrarLogin();
            } else {
                salir = !mostrarMenuPrincipal();
            }
        }
        System.out.println("\nGracias por usar " + facade.getNombreSistema());
    }

    private boolean mostrarLogin() {
        System.out.println("\n========================================");
        System.out.println("  " + facade.getNombreSistema());
        System.out.println("  Mesa de Ayuda - SmartCity Solutions");
        System.out.println("========================================");
        System.out.println("Usuarios de prueba:");
        System.out.println("  admin@helpdesk.com / admin123  (ADMIN)");
        System.out.println("  ana@helpdesk.com   / tec123    (TÉCNICO)");
        System.out.println("  maria@ciudad.com   / cli123    (CLIENTE)");
        System.out.println("----------------------------------------");
        System.out.println("1. Iniciar sesión");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
        String opcion = scanner.nextLine().trim();

        if ("0".equals(opcion)) {
            return false;
        }
        if ("1".equals(opcion)) {
            System.out.print("Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            if (facade.login(email, password)) {
                Usuario u = facade.getUsuarioActual();
                System.out.println("\nBienvenido/a, " + u.getNombre() + " (" + u.getRol() + ")");
            } else {
                System.out.println("Credenciales incorrectas.");
            }
            return true;
        }
        System.out.println("Opción inválida.");
        return true;
    }

    private boolean mostrarMenuPrincipal() {
        Usuario u = facade.getUsuarioActual();
        System.out.println("\n========== MENÚ PRINCIPAL ==========");
        System.out.println("Usuario: " + u.getNombre() + " [" + u.getRol() + "]");
        System.out.println("1. Registrar ticket");
        System.out.println("2. Listar tickets");
        System.out.println("3. Buscar ticket por ID");
        System.out.println("4. Actualizar ticket");
        System.out.println("5. Eliminar ticket");
        System.out.println("6. Asignar técnico");
        System.out.println("7. Avanzar estado del ticket");
        System.out.println("8. Cancelar / cerrar ticket");
        System.out.println("9. Listar usuarios");
        System.out.println("10. Ver historial de notificaciones");
        System.out.println("11. Cerrar sesión");
        System.out.println("0. Salir");
        System.out.print("Opción: ");
        String opcion = scanner.nextLine().trim();

        try {
            switch (opcion) {
                case "1" -> registrarTicket();
                case "2" -> listarTickets();
                case "3" -> buscarTicket();
                case "4" -> actualizarTicket();
                case "5" -> eliminarTicket();
                case "6" -> asignarTecnico();
                case "7" -> avanzarEstado();
                case "8" -> cancelarTicket();
                case "9" -> listarUsuarios();
                case "10" -> facade.mostrarHistorial();
                case "11" -> {
                    facade.logout();
                    System.out.println("Sesión cerrada.");
                }
                case "0" -> {
                    return false;
                }
                default -> System.out.println("Opción inválida.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
        return true;
    }

    private void registrarTicket() {
        System.out.println("\n--- Registrar ticket ---");
        System.out.print("Tipo (INCIDENTE / CONSULTA / MEJORA): ");
        TipoTicket tipo = Validador.parseTipo(scanner.nextLine());
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Descripción: ");
        String descripcion = scanner.nextLine();
        System.out.print("Prioridad (BAJA / MEDIA / ALTA / CRITICA): ");
        Prioridad prioridad = Validador.parsePrioridad(scanner.nextLine());

        String datoExtra;
        switch (tipo) {
            case INCIDENTE -> {
                System.out.print("Sistema afectado: ");
                datoExtra = scanner.nextLine();
            }
            case CONSULTA -> {
                System.out.print("Categoría: ");
                datoExtra = scanner.nextLine();
            }
            default -> {
                System.out.print("Beneficio esperado: ");
                datoExtra = scanner.nextLine();
            }
        }

        Ticket ticket = facade.crearTicket(tipo, titulo, descripcion, prioridad, datoExtra);
        System.out.println("Ticket creado correctamente:");
        System.out.println("  " + ticket);
    }

    private void listarTickets() {
        List<Ticket> tickets = facade.listarTickets();
        System.out.println("\n--- Lista de tickets (" + tickets.size() + ") ---");
        if (tickets.isEmpty()) {
            System.out.println("No hay tickets registrados.");
            return;
        }
        for (Ticket t : tickets) {
            System.out.println("  " + t);
        }
    }

    private void buscarTicket() {
        System.out.print("ID del ticket: ");
        int id = Validador.parseEntero(scanner.nextLine(), "id");
        Ticket ticket = facade.buscarTicket(id);
        if (ticket == null) {
            System.out.println("Ticket no encontrado.");
        } else {
            System.out.println("  " + ticket);
            System.out.println("  Descripción: " + ticket.getDescripcion());
        }
    }

    private void actualizarTicket() {
        System.out.print("ID del ticket: ");
        int id = Validador.parseEntero(scanner.nextLine(), "id");
        System.out.print("Nuevo título (Enter para mantener): ");
        String titulo = scanner.nextLine();
        System.out.print("Nueva descripción (Enter para mantener): ");
        String descripcion = scanner.nextLine();
        System.out.print("Nueva prioridad BAJA/MEDIA/ALTA/CRITICA (Enter para mantener): ");
        String prioridadTxt = scanner.nextLine().trim();
        Prioridad prioridad = prioridadTxt.isEmpty() ? null : Validador.parsePrioridad(prioridadTxt);

        boolean ok = facade.actualizarTicket(id,
                titulo.isBlank() ? null : titulo,
                descripcion.isBlank() ? null : descripcion,
                prioridad);
        System.out.println(ok ? "Ticket actualizado." : "Ticket no encontrado.");
    }

    private void eliminarTicket() {
        System.out.print("ID del ticket a eliminar: ");
        int id = Validador.parseEntero(scanner.nextLine(), "id");
        boolean ok = facade.eliminarTicket(id);
        System.out.println(ok ? "Ticket eliminado." : "No se pudo eliminar el ticket.");
    }

    private void asignarTecnico() {
        System.out.println("\nTécnicos disponibles:");
        for (Usuario t : facade.listarTecnicos()) {
            System.out.println("  " + t);
        }
        System.out.print("ID del ticket: ");
        int ticketId = Validador.parseEntero(scanner.nextLine(), "ticket");
        System.out.print("ID del técnico: ");
        int tecnicoId = Validador.parseEntero(scanner.nextLine(), "técnico");
        boolean ok = facade.asignarTecnico(ticketId, tecnicoId);
        System.out.println(ok ? "Técnico asignado." : "No se pudo asignar (revise IDs o permisos).");
    }

    private void avanzarEstado() {
        System.out.print("ID del ticket: ");
        int id = Validador.parseEntero(scanner.nextLine(), "id");
        boolean ok = facade.avanzarEstado(id);
        if (ok) {
            Ticket t = facade.buscarTicket(id);
            System.out.println("Estado actual: " + (t != null ? t.getEstado().getNombre() : "?"));
        } else {
            System.out.println("No se pudo avanzar el estado (permisos o ID inválido).");
        }
    }

    private void cancelarTicket() {
        System.out.print("ID del ticket: ");
        int id = Validador.parseEntero(scanner.nextLine(), "id");
        boolean ok = facade.cancelarTicket(id);
        System.out.println(ok ? "Operación de cancelación/cierre ejecutada." : "No se pudo cancelar.");
    }

    private void listarUsuarios() {
        System.out.println("\n--- Usuarios ---");
        for (Usuario u : facade.listarUsuarios()) {
            System.out.println("  " + u);
        }
    }
}
