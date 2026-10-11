package dao.impl;

import dao.PrestamoDAO;
import modelo.Prestamo;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa la persistencia de préstamos. Registro y devolución usan un bloqueo estático compartido dentro de la JVM y transacciones que confirman o revierten conjuntamente el préstamo y el stock.
 */
public class PrestamoDAOImpl implements PrestamoDAO {
    // Un único bloqueo para todas las instancias del DAO dentro de esta aplicación.
    private static final Object BLOQUEO_STOCK = new Object();

    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param prestamo registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    @Override
    public boolean guardar(Prestamo prestamo) {
        System.out.println(Thread.currentThread().getName()
                + " está intentando entrar");
        if (prestamo == null || prestamo.getIdEstudiante() == null
                || prestamo.getIdLibro() == null || prestamo.getFechaPrestamo() == null) {
            return false;
        }
        synchronized (BLOQUEO_STOCK) {
            System.out.println(Thread.currentThread().getName()
                    + " entró al bloque");
            try (Connection conn = DatabaseConnection.getInstance().conectar()) {
                conn.setAutoCommit(false);
                try {
                    // El propio UPDATE comprueba la disponibilidad y descuenta.
                    String sqlStock = "UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlStock)) {
                        stmt.setInt(1, prestamo.getIdLibro());
                        if (stmt.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                    String sqlPrestamo = "INSERT INTO prestamos "
                            + "(id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) "
                            + "VALUES (?, ?, ?, ?, FALSE)";
                    int idGenerado = 0;
                    try (PreparedStatement stmt = conn.prepareStatement(sqlPrestamo, Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setInt(1, prestamo.getIdEstudiante());
                        stmt.setInt(2, prestamo.getIdLibro());
                        stmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                        stmt.setDate(4, Date.valueOf(modelo.Prestamo.calcularVencimiento(prestamo.getFechaPrestamo())));
                        if (stmt.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                        try (ResultSet claves = stmt.getGeneratedKeys()) {
                            if (claves.next()) idGenerado = claves.getInt(1);
                        }
                    }
                    // Recién aquí se confirman el préstamo y el descuento juntos.
                    conn.commit();
                    prestamo.setId(idGenerado);
                    prestamo.setDevuelto(false);
                    prestamo.setFechaDevolucion(prestamo.getFechaVencimiento());
                    return true;
                } catch (SQLException | RuntimeException e) {
                    conn.rollback();
                    System.out.println("Error al registrar préstamo: " + e.getMessage());
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error de conexión o reversión: " + e.getMessage());
                return false;
            }
        }
    }
    /**
     * Registra la entrega real y repone stock en una transacción, rechazando una devolución repetida.
     * @param idPrestamo identificador del préstamo
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    @Override
    public boolean devolver(int idPrestamo) {
        return procesarDevolucion(idPrestamo, null);
    }
    /**
     * Registra la devolución solo si el préstamo pertenece al estudiante indicado y aún está pendiente.
     * @param idPrestamo identificador del préstamo
     * @param idEstudiante identificador del estudiante
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    @Override
    public boolean devolverDeEstudiante(int idPrestamo, int idEstudiante) {
        if (idEstudiante <= 0) return false;
        return procesarDevolucion(idPrestamo, idEstudiante);
    }

    /**
     * Verifica pertenencia cuando se indica estudiante y rechaza entregas repetidas. Guarda la fecha real y repone stock dentro del mismo bloqueo y transacción.
     * @param idPrestamo identificador del préstamo
     * @param idEstudiante propietario requerido, o null para la operación global del bibliotecario
     * @return true si la entrega y el stock se confirmaron; false si se rechazó o falló
     */
    private boolean procesarDevolucion(int idPrestamo, Integer idEstudiante) {
        if (idPrestamo <= 0) return false;
        synchronized (BLOQUEO_STOCK) {
            try (Connection conn = DatabaseConnection.getInstance().conectar()) {
                conn.setAutoCommit(false);
                try {
                    int idLibro;
                    String sqlBuscar = "SELECT id_libro, devuelto FROM prestamos WHERE id = ?";
                    if (idEstudiante != null) sqlBuscar += " AND id_estudiante = ?";
                    sqlBuscar += " FOR UPDATE";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlBuscar)) {
                        stmt.setInt(1, idPrestamo);
                        if (idEstudiante != null) stmt.setInt(2, idEstudiante);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (!rs.next() || rs.getBoolean("devuelto")) {
                                conn.rollback();
                                return false;
                            }
                            idLibro = rs.getInt("id_libro");
                        }
                    }
                    String sqlDevolver = "UPDATE prestamos SET devuelto = TRUE, fecha_devolucion = ? WHERE id = ? AND devuelto = FALSE";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlDevolver)) {
                        stmt.setDate(1, Date.valueOf(java.time.LocalDate.now()));
                        stmt.setInt(2, idPrestamo);
                        if (stmt.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                    String sqlStock = "UPDATE libros SET stock = stock + 1 WHERE id = ? AND stock IS NOT NULL";
                    try (PreparedStatement stmt = conn.prepareStatement(sqlStock)) {
                        stmt.setInt(1, idLibro);
                        if (stmt.executeUpdate() != 1) {
                            conn.rollback();
                            return false;
                        }
                    }
                    conn.commit();
                    return true;
                } catch (SQLException | RuntimeException e) {
                    conn.rollback();
                    System.out.println("Error al devolver préstamo: " + e.getMessage());
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error de conexión o reversión: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param prestamo registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    @Override
    public boolean actualizar(Prestamo prestamo) {
        if (prestamo == null || prestamo.getFechaPrestamo() == null) return false;
        String sql = "UPDATE prestamos SET fecha_prestamo = ?, fecha_devolucion = ? "
                + "WHERE id = ? AND devuelto = FALSE";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(prestamo.getFechaPrestamo()));
            stmt.setDate(2, Date.valueOf(prestamo.getFechaVencimiento()));
            stmt.setInt(3, prestamo.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar fecha: " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<Prestamo> listarTodos() {
        List<Prestamo> resultado = new ArrayList<>();
        String sql = "SELECT id, `id_estudiante`, `id_libro`, `fecha_prestamo`, `fecha_devolucion`, `devuelto` FROM prestamos ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar prestamos.", e);
        }
    }

    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Prestamo buscarPorId(int id) {
        String sql = "SELECT id, `id_estudiante`, `id_libro`, `fecha_prestamo`, `fecha_devolucion`, `devuelto` FROM prestamos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar en prestamos.", e);
        }
    }

    /**
     * Consulta los préstamos pendientes y devueltos del estudiante indicado.
     * @param idEstudiante identificador del estudiante
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<Prestamo> listarPorEstudiante(int idEstudiante) {
        List<Prestamo> resultado = new ArrayList<>();
        String sql = "SELECT id, id_estudiante, id_libro, fecha_prestamo, "
                + "fecha_devolucion, devuelto FROM prestamos "
                + "WHERE id_estudiante = ? ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudiante);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) resultado.add(mapear(rs));
            }
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron consultar tus préstamos.", e);
        }
    }
    private Prestamo mapear(ResultSet rs) throws SQLException {
        return new Prestamo(rs.getInt("id"),
                rs.getObject("id_estudiante") == null ? null : rs.getInt("id_estudiante"),
                rs.getObject("id_libro") == null ? null : rs.getInt("id_libro"),
                rs.getDate("fecha_prestamo") == null ? null : rs.getDate("fecha_prestamo").toLocalDate(),
                rs.getDate("fecha_devolucion") == null ? null : rs.getDate("fecha_devolucion").toLocalDate(),
                rs.getObject("devuelto") == null ? null : rs.getBoolean("devuelto"));
    }
}
