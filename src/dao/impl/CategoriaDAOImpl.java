package dao.impl;

import dao.CategoriaDAO;
import modelo.Categoria;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa con JDBC las operaciones de categoria. Usa sentencias parametrizadas y cierra conexiones, sentencias y resultados mediante try-with-resources.
 */
public class CategoriaDAOImpl implements CategoriaDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    @Override
    public boolean guardar(Categoria entidad) {
        String sql = "INSERT INTO categorias (`nombre`) VALUES (?)";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(stmt, entidad);
            if (stmt.executeUpdate() != 1) return false;
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) entidad.setId(claves.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudo guardar en categorias. " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<Categoria> listarTodos() {
        List<Categoria> resultado = new ArrayList<>();
        String sql = "SELECT id, `nombre` FROM categorias ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar categorias.", e);
        }
    }

    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Categoria buscarPorId(int id) {
        String sql = "SELECT id, `nombre` FROM categorias WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar en categorias.", e);
        }
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    @Override
    public boolean actualizar(Categoria entidad) {
        String sql = "UPDATE categorias SET `nombre` = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            asignarParametros(stmt, entidad);
            stmt.setInt(2, entidad.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo actualizar categorias. " + e.getMessage());
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
        String sql = "DELETE FROM categorias WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo eliminar de categorias. Revisa las relaciones existentes. " + e.getMessage());
            return false;
        }
    }

    private void asignarParametros(PreparedStatement stmt, Categoria entidad) throws SQLException {
        stmt.setString(1, entidad.getNombre());
    }

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"),
                rs.getString("nombre"));
    }
}
