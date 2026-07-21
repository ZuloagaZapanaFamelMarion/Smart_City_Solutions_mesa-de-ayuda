package vista;

import facade.HelpDeskFacade;
import modelo.Usuario;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Vista Swing de inicio de sesión (MVC).
 * SRP: solo interacción con el usuario; delega en el Facade.
 */
public class VentanaLogin extends JFrame {
    private final HelpDeskFacade facade;
    private final JTextField campoEmail = new JTextField(20);
    private final JPasswordField campoPassword = new JPasswordField(20);

    public VentanaLogin(HelpDeskFacade facade) {
        super("Help Desk - SmartCity Solutions");
        this.facade = facade;
        construirUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void construirUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Mesa de Ayuda", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        JLabel subtitulo = new JLabel("SmartCity Solutions", SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(subtitulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(campoEmail, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(campoPassword, gbc);

        JButton botonEntrar = new JButton("Iniciar sesión");
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(botonEntrar, gbc);

        JLabel ayuda = new JLabel(
                "<html><small>Usuarios de prueba:<br>"
                        + "admin@helpdesk.com / admin123 (ADMIN)<br>"
                        + "ana@helpdesk.com / tec123 (TÉCNICO)<br>"
                        + "maria@ciudad.com / cli123 (CLIENTE)</small></html>");
        gbc.gridy = 5;
        panel.add(ayuda, gbc);

        botonEntrar.addActionListener(e -> intentarLogin());
        getRootPane().setDefaultButton(botonEntrar);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }

    private void intentarLogin() {
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword()).trim();
        if (facade.login(email, password)) {
            Usuario u = facade.getUsuarioActual();
            JOptionPane.showMessageDialog(this,
                    "Bienvenido/a, " + u.getNombre() + " (" + u.getRol() + ")",
                    "Sesión iniciada", JOptionPane.INFORMATION_MESSAGE);
            new VentanaPrincipal(facade).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            campoPassword.setText("");
        }
    }
}
