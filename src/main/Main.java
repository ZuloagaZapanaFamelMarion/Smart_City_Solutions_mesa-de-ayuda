package main;

import facade.HelpDeskFacade;
import vista.MenuPrincipal;

/**
 * Punto de entrada del Sistema de Mesa de Ayuda.
 * Curso: Diseño de Patrones - SmartCity Solutions
 */
public class Main {
    public static void main(String[] args) {
        HelpDeskFacade facade = new HelpDeskFacade();
        MenuPrincipal menu = new MenuPrincipal(facade);
        menu.iniciar();
    }
}
