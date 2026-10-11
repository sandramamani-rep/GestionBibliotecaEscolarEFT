package dao.impl;

import dao.LibroDAO;
import modelo.Libro;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa con JDBC las operaciones de libro. Usa sentencias parametrizadas y cierra conexiones, sentencias y resultados mediante try-with-resources.
 */
public class LibroDAOImpl implements LibroDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    @Override
    public boolean guardar(Libro entidad) {
        String sql = "INSERT INTO libros (`titulo`, `autor`, `isbn`, `editorial`, `stock`, `id_categoria`) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(stmt, entidad);
            if (stmt.executeUpdate() != 1) return false;
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) entidad.setId(claves.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudo guardar en libros. " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<Libro> listarTodos() {
        List<Libro> resultado = new ArrayList<>();
        String sql = "SELECT id, `titulo`, `autor`, `isbn`, `editorial`, `stock`, `id_categoria` FROM libros ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar libros.", e);
        }
    }

    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Libro buscarPorId(int id) {
        String sql = "SELECT id, `titulo`, `autor`, `isbn`, `editorial`, `stock`, `id_categoria` FROM libros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar en libros.", e);
        }
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    @Override
    public boolean actualizar(Libro entidad) {
        String sql = "UPDATE libros SET `titulo` = ?, `autor` = ?, `isbn` = ?, `editorial` = ?, `stock` = ?, `id_categoria` = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            asignarParametros(stmt, entidad);
            stmt.setInt(7, entidad.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo actualizar libros. " + e.getMessage());
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
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo eliminar de libros. Revisa las relaciones existentes. " + e.getMessage());
            return false;
        }
    }

    private void asignarParametros(PreparedStatement stmt, Libro entidad) throws SQLException {
        stmt.setString(1, entidad.getTitulo());
        stmt.setString(2, entidad.getAutor());
        stmt.setString(3, entidad.getIsbn());
        stmt.setString(4, entidad.getEditorial());
        stmt.setObject(5, entidad.getStock(), java.sql.Types.INTEGER);
        stmt.setObject(6, entidad.getIdCategoria(), java.sql.Types.INTEGER);
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        return new Libro(rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("isbn"),
                rs.getString("editorial"),
                rs.getObject("stock") == null ? null : rs.getInt("stock"),
                rs.getObject("id_categoria") == null ? null : rs.getInt("id_categoria"));
    }
}
