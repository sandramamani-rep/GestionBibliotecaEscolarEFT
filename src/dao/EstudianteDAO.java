package dao;

import modelo.Estudiante;
import java.util.List;

/**
 * Define las operaciones persistentes de estudiante. Las consultas propagan IllegalStateException ante errores JDBC; las escrituras indican el resultado con un booleano.
 */
public interface EstudianteDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    boolean guardar(Estudiante entidad);
    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<Estudiante> listarTodos();
    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    Estudiante buscarPorId(int id);

    /**
     * Busca el estudiante cuyo RUT coincide con el indicado.
     * @param rut RUT con el formato almacenado en la base de datos
     * @return estudiante encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    Estudiante buscarPorRut(String rut);
    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    boolean actualizar(Estudiante entidad);
    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    boolean eliminar(int id);
    /**
     * Consulta estudiantes cuyo RUT aún no pertenece a una cuenta de usuario.
     * @return estudiantes disponibles para crear una cuenta
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<Estudiante> listarSinUsuario();

}
