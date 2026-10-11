package dao;

import modelo.Usuario;
import java.util.List;

/**
 * Define las operaciones persistentes de usuario. Las consultas propagan IllegalStateException ante errores JDBC; las escrituras indican el resultado con un booleano.
 */
public interface UsuarioDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    boolean guardar(Usuario entidad);
    /**
     * Busca la cuenta por RUT y verifica su contraseña.
     * @param rut RUT con el formato almacenado en la base de datos
     * @param contrasenia contraseña de la cuenta
     * @return usuario autenticado, o null si las credenciales no coinciden
     * @throws IllegalStateException si falla la consulta JDBC
     */
    Usuario autenticar(String rut, String contrasenia);
    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<Usuario> listarTodos();
    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    Usuario buscarPorId(int id);
    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    boolean actualizar(Usuario entidad);
    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    boolean eliminar(int id);
}
