package dao.impl;

import dao.ReporteDAO;
import modelo.ReporteLibro;
import modelo.ReportePrestamo;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Obtiene reportes mediante consultas JDBC parametrizadas. Los historiales se filtran por el identificador del estudiante.
 */
public class ReporteDAOImpl implements ReporteDAO {
    private static final String CONSULTA_PRESTAMOS =
            "SELECT p.id, e.nombre AS estudiante, e.rut, l.titulo AS libro, "
                    + "p.fecha_prestamo, p.fecha_devolucion, p.devuelto "
                    + "FROM prestamos p "
                    + "LEFT JOIN estudiantes e ON e.id = p.id_estudiante "
                    + "LEFT JOIN libros l ON l.id = p.id_libro ";

    /**
     * Cuenta préstamos pendientes y devueltos por libro y ordena por frecuencia descendente.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<ReporteLibro> librosMasPrestados() {
        List<ReporteLibro> resultado = new ArrayList<>();
        // Cuenta tanto préstamos pendientes como devueltos, agrupados por libro.
        String sql = "SELECT l.id, l.titulo, l.autor, l.isbn, COUNT(p.id) AS cantidad "
                + "FROM libros l INNER JOIN prestamos p ON p.id_libro = l.id "
                + "GROUP BY l.id, l.titulo, l.autor, l.isbn "
                + "ORDER BY cantidad DESC, l.titulo ASC, l.id ASC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                resultado.add(new ReporteLibro(rs.getInt("id"), rs.getString("titulo"),
                        rs.getString("autor"), rs.getString("isbn"), rs.getLong("cantidad")));
            }
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar los libros más prestados. Revisa la conexión.", e);
        }
    }

    /**
     * Consulta el historial filtrado por el ID del estudiante.
     * @param idEstudiante identificador del estudiante
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<ReportePrestamo> historialPorEstudiante(int idEstudiante) {
        List<ReportePrestamo> resultado = new ArrayList<>();
        String sql = CONSULTA_PRESTAMOS + "WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC, p.id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudiante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) resultado.add(mapearPrestamo(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar el historial del estudiante. Revisa la conexión.", e);
        }
    }

    /**
     * Consulta préstamos no devueltos, ordenados por vencimiento.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<ReportePrestamo> prestamosPendientes() {
        List<ReportePrestamo> resultado = new ArrayList<>();
        String sql = CONSULTA_PRESTAMOS + "WHERE p.devuelto = FALSE ORDER BY p.fecha_prestamo ASC, p.id ASC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapearPrestamo(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo consultar los préstamos pendientes. Revisa la conexión.", e);
        }
    }

    private ReportePrestamo mapearPrestamo(ResultSet rs) throws SQLException {
        Date inicio = rs.getDate("fecha_prestamo");
        Date entrega = rs.getDate("fecha_devolucion");
        return new ReportePrestamo(rs.getInt("id"), rs.getString("estudiante"),
                rs.getString("rut"), rs.getString("libro"),
                inicio == null ? null : inicio.toLocalDate(),
                entrega == null ? null : entrega.toLocalDate(), rs.getBoolean("devuelto"));
    }
}
