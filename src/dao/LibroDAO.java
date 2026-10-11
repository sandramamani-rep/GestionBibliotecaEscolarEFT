package dao;

import modelo.Libro;
import java.util.List;

/**
 * Define las operaciones persistentes de libro. Las consultas propagan IllegalStateException ante errores JDBC; las escrituras indican el resultado con un booleano.
 */
public interface LibroDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    boolean guardar(Libro entidad);
    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<Libro> listarTodos();
    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    Libro buscarPorId(int id);
    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    boolean actualizar(Libro entidad);
    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    boolean eliminar(int id);
}
