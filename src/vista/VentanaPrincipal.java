package vista;

import facade.HelpDeskFacade;
import modelo.Prioridad;
import modelo.Ticket;
import modelo.TipoTicket;
import modelo.Usuario;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Ventana principal Swing (MVC).
 * SRP: solo interacción con el usuario; toda la lógica vive en el Facade.
 */
public class VentanaPrincipal extends JFrame {
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final HelpDeskFacade facade;
    private final DefaultTableModel modeloTabla;
    private final JTable tabla;
    private final JLabel etiquetaUsuario = new JLabel();

    public VentanaPrincipal(HelpDeskFacade facade) {
        super("Help Desk - SmartCity Solutions");
        this.facade = facade;

        String[] columnas = {"ID", "Tipo", "Título", "Detalle", "Prioridad",
                "Estado", "Solicitante", "Técnico", "Creación"};
        this.modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        this.tabla = new JTable(modeloTabla);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getColumnModel().getColumn(0).setMaxWidth(50);

        construirUI();
        refrescarTabla();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 560);
        setLocationRelativeTo(null);
    }

    private void construirUI() {
        setLayout(new BorderLayout(8, 8));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBorder(BorderFactory.createEmptyBorder(10, 12, 0, 12));
        Usuario u = facade.getUsuarioActual();
        etiquetaUsuario.setText("Usuario: " + u.getNombre() + "  [" + u.getRol() + "]");
        etiquetaUsuario.setFont(etiquetaUsuario.getFont().deriveFont(Font.BOLD, 14f));
        cabecera.add(etiquetaUsuario, BorderLayout.WEST);

        JButton botonSalir = new JButton("Cerrar sesión");
        botonSalir.addActionListener(e -> cerrarSesion());
        cabecera.add(botonSalir, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel botones = new JPanel(new GridLayout(2, 5, 8, 8));
        botones.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));
        botones.add(crearBoton("Registrar ticket", this::registrarTicket));
        botones.add(crearBoton("Actualizar", this::actualizarTicket));
        botones.add(crearBoton("Eliminar", this::eliminarTicket));
        botones.add(crearBoton("Asignar técnico", this::asignarTecnico));
        botones.add(crearBoton("Avanzar estado", this::avanzarEstado));
        botones.add(crearBoton("Cancelar/Cerrar", this::cancelarTicket));
        botones.add(crearBoton("Usuarios", this::listarUsuarios));
        botones.add(crearBoton("Historial", this::verHistorial));
        botones.add(crearBoton("Refrescar", this::refrescarTabla));
        add(botones, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Runnable accion) {
        JButton boton = new JButton(texto);
        boton.addActionListener(e -> {
            try {
                accion.run();
            } catch (IllegalArgumentException | IllegalStateException ex) {
                mostrarError(ex.getMessage());
            }
        });
        return boton;
    }

    private void refrescarTabla() {
        modeloTabla.setRowCount(0);
        for (Ticket t : facade.listarTickets()) {
            Usuario tecnico = t.getTecnicoAsignado();
            modeloTabla.addRow(new Object[]{
                    t.getId(), t.getTipo(), t.getTitulo(), t.getDetalleTipo(),
                    t.getPrioridad(), t.getEstado().getNombre(),
                    t.getSolicitante().getNombre(),
                    tecnico == null ? "Sin asignar" : tecnico.getNombre(),
                    t.getFechaCreacion().format(FECHA)
            });
        }
    }

    private Integer idSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError("Seleccione un ticket en la tabla.");
            return null;
        }
        return (Integer) modeloTabla.getValueAt(fila, 0);
    }

    private void registrarTicket() {
        JComboBox<TipoTicket> comboTipo = new JComboBox<>(TipoTicket.values());
        JTextField campoTitulo = new JTextField(25);
        JTextField campoDescripcion = new JTextField(25);
        JComboBox<Prioridad> comboPrioridad = new JComboBox<>(Prioridad.values());
        JTextField campoExtra = new JTextField(25);
        JLabel etiquetaExtra = new JLabel("Sistema afectado:");

        comboTipo.addActionListener(e -> {
            TipoTicket tipo = (TipoTicket) comboTipo.getSelectedItem();
            switch (tipo) {
                case INCIDENTE -> etiquetaExtra.setText("Sistema afectado:");
                case CONSULTA -> etiquetaExtra.setText("Categoría:");
                default -> etiquetaExtra.setText("Beneficio esperado:");
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Tipo:"));
        panel.add(comboTipo);
        panel.add(new JLabel("Título:"));
        panel.add(campoTitulo);
        panel.add(new JLabel("Descripción:"));
        panel.add(campoDescripcion);
        panel.add(new JLabel("Prioridad:"));
        panel.add(comboPrioridad);
        panel.add(etiquetaExtra);
        panel.add(campoExtra);

        int opcion = JOptionPane.showConfirmDialog(this, panel,
                "Registrar ticket", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        Ticket ticket = facade.crearTicket(
                (TipoTicket) comboTipo.getSelectedItem(),
                campoTitulo.getText(),
                campoDescripcion.getText(),
                (Prioridad) comboPrioridad.getSelectedItem(),
                campoExtra.getText());
        refrescarTabla();
        mostrarInfo("Ticket #" + ticket.getId() + " creado correctamente.");
    }

    private void actualizarTicket() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        Ticket actual = facade.buscarTicket(id);
        JTextField campoTitulo = new JTextField(actual.getTitulo(), 25);
        JTextField campoDescripcion = new JTextField(actual.getDescripcion(), 25);
        JComboBox<Prioridad> comboPrioridad = new JComboBox<>(Prioridad.values());
        comboPrioridad.setSelectedItem(actual.getPrioridad());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Título:"));
        panel.add(campoTitulo);
        panel.add(new JLabel("Descripción:"));
        panel.add(campoDescripcion);
        panel.add(new JLabel("Prioridad:"));
        panel.add(comboPrioridad);

        int opcion = JOptionPane.showConfirmDialog(this, panel,
                "Actualizar ticket #" + id, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        boolean ok = facade.actualizarTicket(id,
                campoTitulo.getText().isBlank() ? null : campoTitulo.getText(),
                campoDescripcion.getText().isBlank() ? null : campoDescripcion.getText(),
                (Prioridad) comboPrioridad.getSelectedItem());
        refrescarTabla();
        if (ok) {
            mostrarInfo("Ticket actualizado.");
        } else {
            mostrarError("Ticket no encontrado.");
        }
    }

    private void eliminarTicket() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el ticket #" + id + "?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        boolean ok = facade.eliminarTicket(id);
        refrescarTabla();
        if (ok) {
            mostrarInfo("Ticket eliminado.");
        } else {
            mostrarError("Acceso denegado: solo TÉCNICO o ADMINISTRADOR puede eliminar.");
        }
    }

    private void asignarTecnico() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        List<Usuario> tecnicos = facade.listarTecnicos();
        JComboBox<Usuario> comboTecnicos = new JComboBox<>(tecnicos.toArray(new Usuario[0]));
        int opcion = JOptionPane.showConfirmDialog(this, comboTecnicos,
                "Asignar técnico al ticket #" + id,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
        Usuario tecnico = (Usuario) comboTecnicos.getSelectedItem();
        boolean ok = facade.asignarTecnico(id, tecnico.getId());
        refrescarTabla();
        if (ok) {
            mostrarInfo("Técnico asignado.");
        } else {
            mostrarError("No se pudo asignar (revise permisos).");
        }
    }

    private void avanzarEstado() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        boolean ok = facade.avanzarEstado(id);
        refrescarTabla();
        if (ok) {
            Ticket t = facade.buscarTicket(id);
            mostrarInfo("Estado actual: " + (t != null ? t.getEstado().getNombre() : "?"));
        } else {
            mostrarError("No se pudo avanzar el estado (permisos o estado final).");
        }
    }

    private void cancelarTicket() {
        Integer id = idSeleccionado();
        if (id == null) {
            return;
        }
        boolean ok = facade.cancelarTicket(id);
        refrescarTabla();
        if (ok) {
            mostrarInfo("Operación de cancelación/cierre ejecutada.");
        } else {
            mostrarError("No se pudo cancelar (permisos o ticket ajeno).");
        }
    }

    private void listarUsuarios() {
        StringBuilder sb = new StringBuilder();
        for (Usuario u : facade.listarUsuarios()) {
            sb.append(u).append('\n');
        }
        mostrarTexto("Usuarios", sb.toString());
    }

    private void verHistorial() {
        List<String> historial = facade.getHistorial();
        if (historial.isEmpty()) {
            mostrarInfo("No hay notificaciones registradas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String item : historial) {
            sb.append("- ").append(item).append('\n');
        }
        mostrarTexto("Historial de notificaciones", sb.toString());
    }

    private void cerrarSesion() {
        facade.logout();
        new VentanaLogin(facade).setVisible(true);
        dispose();
    }

    private void mostrarTexto(String titulo, String contenido) {
        JTextArea area = new JTextArea(contenido, 15, 60);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                titulo, JOptionPane.PLAIN_MESSAGE);
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}
