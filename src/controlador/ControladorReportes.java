package controlador;

import dao.ReporteDAO;
import dao.impl.ReporteDAOImpl;
import modelo.ReporteLibro;
import modelo.ReportePrestamo;
import java.util.List;

/**
 * Expone las consultas de reportes y valida el identificador utilizado para el historial individual.
 */
public class ControladorReportes {
    private final ReporteDAO reporteDAO = new ReporteDAOImpl();

    /**
     * Obtiene el reporte de frecuencia de préstamos por libro.
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<ReporteLibro> obtenerLibrosMasPrestados() {
        return reporteDAO.librosMasPrestados();
    }

    /**
     * Obtiene el historial individual del estudiante indicado.
     * @param idEstudiante identificador del estudiante
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<ReportePrestamo> obtenerHistorial(int idEstudiante) {
        if (idEstudiante <= 0) throw new IllegalArgumentException("Selecciona un estudiante.");
        return reporteDAO.historialPorEstudiante(idEstudiante);
    }

    /**
     * Obtiene el reporte de préstamos aún no devueltos.
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<ReportePrestamo> obtenerPrestamosPendientes() {
        return reporteDAO.prestamosPendientes();
    }
}
