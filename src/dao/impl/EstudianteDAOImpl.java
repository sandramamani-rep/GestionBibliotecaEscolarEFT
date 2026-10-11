package dao.impl;

import dao.EstudianteDAO;
import modelo.Estudiante;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa con JDBC las operaciones de estudiante. Usa sentencias parametrizadas y cierra conexiones, sentencias y resultados mediante try-with-resources.
 */
public class EstudianteDAOImpl implements EstudianteDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    @Override
    public boolean guardar(Estudiante entidad) {
        String sql = "INSERT INTO estudiantes (`nombre`, `rut`, `curso`, `correo`) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(stmt, entidad);
            if (stmt.executeUpdate() != 1) return false;
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) entidad.setId(claves.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudo guardar en estudiantes. " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<Estudiante> listarTodos() {
        List<Estudiante> resultado = new ArrayList<>();
        String sql = "SELECT id, `nombre`, `rut`, `curso`, `correo` FROM estudiantes ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar estudiantes.", e);
        }
    }

    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Estudiante buscarPorId(int id) {
        String sql = "SELECT id, `nombre`, `rut`, `curso`, `correo` FROM estudiantes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar en estudiantes.", e);
        }
    }

    /**
     * Busca el estudiante cuyo RUT coincide con el indicado.
     * @param rut RUT con el formato almacenado en la base de datos
     * @return estudiante encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Estudiante buscarPorRut(String rut) {
        String sql = "SELECT id, nombre, rut, curso, correo FROM estudiantes WHERE rut = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, rut);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar el estudiante por RUT.", e);
        }
    }
    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    @Override
    public boolean actualizar(Estudiante entidad) {
        String sql = "UPDATE estudiantes SET `nombre` = ?, `rut` = ?, `curso` = ?, `correo` = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            asignarParametros(stmt, entidad);
            stmt.setInt(5, entidad.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo actualizar estudiantes. " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM estudiantes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo eliminar de estudiantes. Revisa las relaciones existentes. " + e.getMessage());
            return false;
        }
    }

    private void asignarParametros(PreparedStatement stmt, Estudiante entidad) throws SQLException {
        stmt.setString(1, entidad.getNombre());
        stmt.setString(2, entidad.getRut());
        stmt.setString(3, entidad.getCurso());
        stmt.setString(4, entidad.getCorreo());
    }

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("curso"),
                rs.getString("correo"));
    }
    /**
     * Busca estudiantes sin una cuenta asociada por RUT, sin depender de los IDs.
     * @return estudiantes sin usuario, ordenados por nombre y RUT
     * @throws IllegalStateException si falla la consulta
     */
    @Override
    public List<Estudiante> listarSinUsuario() {
        List<Estudiante> resultado = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, e.rut, e.curso, e.correo "
                + "FROM estudiantes e WHERE e.rut IS NOT NULL AND TRIM(e.rut) <> '' "
                + "AND NOT EXISTS (SELECT 1 FROM usuarios u WHERE u.rut = e.rut) "
                + "ORDER BY e.nombre, e.rut";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudieron cargar los estudiantes sin usuario.", e);
        }
    }

}
