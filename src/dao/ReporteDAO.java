package dao;

import modelo.ReporteLibro;
import modelo.ReportePrestamo;
import java.util.List;

/**
 * Define consultas de reportes sin modificar los registros ni el stock. Los fallos JDBC se comunican mediante IllegalStateException.
 */
public interface ReporteDAO {
    /**
     * Cuenta préstamos pendientes y devueltos por libro y ordena por frecuencia descendente.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<ReporteLibro> librosMasPrestados();
    /**
     * Consulta el historial filtrado por el ID del estudiante.
     * @param idEstudiante identificador del estudiante
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<ReportePrestamo> historialPorEstudiante(int idEstudiante);
    /**
     * Consulta préstamos no devueltos, ordenados por vencimiento.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    List<ReportePrestamo> prestamosPendientes();
}
