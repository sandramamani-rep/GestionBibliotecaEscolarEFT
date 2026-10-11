package controlador;

import dao.UsuarioDAO;
import dao.impl.UsuarioDAOImpl;
import modelo.Usuario;
import java.util.List;

/**
 * Valida los datos de usuarios y delega su persistencia en el DAO correspondiente.
 */
public class ControladorUsuarios {
    private final UsuarioDAO usuarioDAO;

    /**
     * Crea una instancia de ControladorUsuarios.
     */
    public ControladorUsuarios() {
        usuarioDAO = new UsuarioDAOImpl();
    }

    /**
     * Valida los datos y registra usuario.
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param correo correo electrónico
     * @param contrasenia contraseña de la cuenta
     * @param rol rol de acceso: bibliotecario o estudiante
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public boolean registrarUsuario(String nombre, String rut, String correo, String contrasenia, String rol) {
        if (nombre == null
                || nombre.isBlank()
                || rut == null
                || rut.isBlank()
                || correo == null
                || correo.isBlank()
                || contrasenia == null
                || contrasenia.isBlank()
                || rol == null
                || rol.isBlank()
                || (!rol.equals("bibliotecario") && !rol.equals("estudiante"))) {
            return false;
        }
        Usuario usuario = new Usuario(nombre.trim(), rut.trim(), correo.trim(), contrasenia, rol.trim());
        return usuarioDAO.guardar(usuario);
    }

    /**
     * Consulta los registros de usuarios.
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.listarTodos();
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param id identificador del registro
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param correo correo electrónico
     * @param contrasenia nueva contraseña; null o vacía conserva la actual
     * @param rol rol de acceso: bibliotecario o estudiante
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    public boolean actualizar(int id, String nombre, String rut, String correo, String contrasenia, String rol) {
        if (id <= 0) {
            return false;
        }
        // Si la contraseña queda vacía, conservamos la guardada.
        if (contrasenia == null || contrasenia.isEmpty()) {
            Usuario actual = usuarioDAO.buscarPorId(id);
            if (actual == null) {
                return false;
            }
            contrasenia = actual.getContrasenia();
        }
        if (nombre == null
                || nombre.isBlank()
                || rut == null
                || rut.isBlank()
                || correo == null
                || correo.isBlank()
                || contrasenia == null
                || contrasenia.isBlank()
                || rol == null
                || rol.isBlank()
                || (!rol.equals("bibliotecario") && !rol.equals("estudiante"))) {
            return false;
        }
        Usuario usuario = new Usuario(id, nombre.trim(), rut.trim(), correo.trim(), contrasenia, rol.trim());
        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    public boolean eliminar(int id) {
        if (id <= 0) {
            return false;
        }
        return usuarioDAO.eliminar(id);
    }
}
