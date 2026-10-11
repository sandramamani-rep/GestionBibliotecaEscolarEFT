package controlador;
import dao.EstudianteDAO;
import dao.UsuarioDAO;
import dao.impl.EstudianteDAOImpl;
import dao.impl.UsuarioDAOImpl;
import modelo.Estudiante;
import modelo.Usuario;

/**
 * Valida credenciales y rol. Para una cuenta de estudiante exige un registro asociado por RUT y conserva dicho registro para la sesión.
 */
public class ControladorLogin {
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final EstudianteDAO estudianteDAO = new EstudianteDAOImpl();
    private Estudiante estudianteActual;
    private String mensajeError;

    /**
     * Valida RUT, contraseña, rol y, cuando corresponde, el registro del estudiante. Reinicia el estado del intento anterior.
     * @param rut RUT con el formato almacenado en la base de datos
     * @param clave contraseña ingresada
     * @return usuario sin contraseña si se autoriza el acceso, o null si se rechaza
     */
    public Usuario autenticar(String rut, String clave) {
        estudianteActual = null;
        mensajeError = "RUT o contraseña incorrectos.";
        if (rut == null || rut.isBlank() || clave == null || clave.isEmpty()) {
            return null;
        }
        Usuario usuario = usuarioDAO.autenticar(rut.trim(), clave);
        if (usuario == null) return null;
        usuario.setContrasenia(null);
        if ("estudiante".equals(usuario.getRol())) {
            Estudiante estudiante = estudianteDAO.buscarPorRut(usuario.getRut());
            if (estudiante == null) {
                mensajeError = "Tu cuenta no tiene un registro de estudiante asociado. Solicita al bibliotecario que complete tu registro.";
                return null;
            }
            estudianteActual = estudiante;
        } else if (!"bibliotecario".equals(usuario.getRol())) {
            mensajeError = "El usuario no tiene un rol válido.";
            return null;
        }
        mensajeError = null;
        return usuario;
    }

    /**
     * Obtiene el estudiante asociado al último acceso correcto.
     * @return estudiante asociado, o null si no hay uno
     */
    public Estudiante getEstudianteActual() {
        return estudianteActual;
    }

    /**
     * Obtiene la explicación del último rechazo de autenticación.
     * @return mensaje del rechazo, o null tras un acceso correcto
     */
    public String getMensajeError() {
        return mensajeError;
    }
}
