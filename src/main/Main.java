package main;

import facade.HelpDeskFacade;
import vista.MenuPrincipal;
import vista.VentanaLogin;

import javax.swing.SwingUtilities;

/**
 * Punto de entrada del Sistema de Mesa de Ayuda.
 * Curso: Diseño de Patrones - SmartCity Solutions
 *
 * Por defecto abre la interfaz gráfica (Java Swing).
 * Con el argumento "consola" usa el menú de texto.
 */
public class Main {
    public static void main(String[] args) {
        HelpDeskFacade facade = new HelpDeskFacade();

        if (args.length > 0 && "consola".equalsIgnoreCase(args[0])) {
            MenuPrincipal menu = new MenuPrincipal(facade);
            menu.iniciar();
        } else {
            SwingUtilities.invokeLater(() -> new VentanaLogin(facade).setVisible(true));
        }
    }
}
