package vista;
import controlador.ControladorLogin;
import modelo.Usuario;
import javax.swing.*;

/**
 * Captura RUT y contraseña y abre la biblioteca después de validar el acceso.
 */
public class Login extends JFrame {
    private JPanel login;
    private JTextField txtRut;
    private JPasswordField txtContrasenia;
    private JButton btnAccederLogin;
    private JButton btnCancelarLogin;
    private JLabel lblRut;
    private JLabel lblContrasenia;
    private final ControladorLogin controladorLogin = new ControladorLogin();

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     */
    public Login() {
        setTitle("Biblioteca escolar - Iniciar sesión");
        setContentPane(login);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 230);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnAccederLogin);
        txtRut.setToolTipText("Ingresa tu RUT con el formato registrado");
        btnAccederLogin.addActionListener(e -> acceder());
        btnCancelarLogin.addActionListener(e -> dispose());
    }
    private void acceder() {
        try {
            String rut = txtRut.getText().trim();
            String clave = new String(txtContrasenia.getPassword());
            Usuario usuario = controladorLogin.autenticar(rut, clave);
            if (usuario == null) {
                JOptionPane.showMessageDialog(this, controladorLogin.getMensajeError());
                return;
            }
            new VentanaBiblioteca(usuario,
                    controladorLogin.getEstudianteActual()).setVisible(true);
            dispose();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
        } finally {
            txtContrasenia.setText("");
        }
    }
}
