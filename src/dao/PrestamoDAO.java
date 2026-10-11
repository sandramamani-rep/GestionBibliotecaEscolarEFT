package dao;

import modelo.Prestamo;
import java.util.List;

/**
 * Define el registro, la consulta, la actualización de fechas y la devolución de préstamos. Las consultas propagan errores JDBC; las escrituras informan su resultado con un booleano.
 */
public interface PrestamoDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    boolean guardar(Prestamo entidad);
    /**
     * Registra la entrega real y repone stock en una transacción, rechazando una devolución repetida.
     * @param idPrestamo identificador del préstamo
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    boolean devolver(int idPrestamo);
    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<Prestamo> listarTodos();
    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    Prestamo buscarPorId(int id);
    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    boolean actualizar(Prestamo entidad);
    /**
     * Consulta los préstamos pendientes y devueltos del estudiante indicado.
     * @param idEstudiante identificador del estudiante
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<Prestamo> listarPorEstudiante(int idEstudiante);
    /**
     * Registra la devolución solo si el préstamo pertenece al estudiante indicado y aún está pendiente.
     * @param idPrestamo identificador del préstamo
     * @param idEstudiante identificador del estudiante
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    boolean devolverDeEstudiante(int idPrestamo, int idEstudiante);
}