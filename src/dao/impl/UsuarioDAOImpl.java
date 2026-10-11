package dao.impl;

import dao.UsuarioDAO;
import modelo.Usuario;
import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementa con JDBC las operaciones de usuario. Usa sentencias parametrizadas y cierra conexiones, sentencias y resultados mediante try-with-resources.
 */
public class UsuarioDAOImpl implements UsuarioDAO {
    /**
     * Guarda un registro y asigna al objeto el identificador generado.
     * @param entidad registro que se desea persistir
     * @return true si se guardó el registro; false si no se pudo guardar
     */
    @Override
    public boolean guardar(Usuario entidad) {
        String sql = "INSERT INTO usuarios (`nombre`, `rut`, `correo`, `contraseña`, `rol`) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignarParametros(stmt, entidad);
            if (stmt.executeUpdate() != 1) return false;
            try (ResultSet claves = stmt.getGeneratedKeys()) {
                if (claves.next()) entidad.setId(claves.getInt(1));
            }
            return true;
        } catch (SQLException e) {
            System.out.println("No se pudo guardar en usuarios. " + e.getMessage());
            return false;
        }
    }

    /**
     * Consulta los registros disponibles.
     * @return lista de resultados, vacía si no hay coincidencias
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public List<Usuario> listarTodos() {
        List<Usuario> resultado = new ArrayList<>();
        String sql = "SELECT id, `nombre`, `rut`, `correo`, `contraseña`, `rol` FROM usuarios ORDER BY id DESC";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
            return resultado;
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo listar usuarios.", e);
        }
    }

    /**
     * Busca un registro por su identificador.
     * @param id identificador del registro
     * @return registro encontrado, o null si no existe
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Usuario buscarPorId(int id) {
        String sql = "SELECT id, `nombre`, `rut`, `correo`, `contraseña`, `rol` FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("No se pudo buscar en usuarios.", e);
        }
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param entidad registro que se desea persistir
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    @Override
    public boolean actualizar(Usuario entidad) {
        String sql = "UPDATE usuarios SET `nombre` = ?, `rut` = ?, `correo` = ?, `contraseña` = ?, `rol` = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            asignarParametros(stmt, entidad);
            stmt.setInt(6, entidad.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo actualizar usuarios. " + e.getMessage());
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
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("No se pudo eliminar de usuarios. Revisa las relaciones existentes. " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca la cuenta por RUT y verifica su contraseña.
     * @param rut RUT con el formato almacenado en la base de datos
     * @param contrasenia contraseña de la cuenta
     * @return usuario autenticado, o null si las credenciales no coinciden
     * @throws IllegalStateException si falla la consulta JDBC
     */
    @Override
    public Usuario autenticar(String rut, String contrasenia) {
        String sql = "SELECT id, nombre, rut, correo, `contraseña`, rol "
                + "FROM usuarios WHERE rut = ?";

        try (Connection conn = DatabaseConnection.getInstance().conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rut);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapear(rs);

                    if (contrasenia != null
                            && contrasenia.equals(usuario.getContrasenia())) {
                        return usuario;
                    }
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo conectar para iniciar sesión.", e);
        }
    }

    private void asignarParametros(PreparedStatement stmt, Usuario entidad) throws SQLException {
        stmt.setString(1, entidad.getNombre());
        stmt.setString(2, entidad.getRut());
        stmt.setString(3, entidad.getCorreo());
        stmt.setString(4, entidad.getContrasenia());
        stmt.setString(5, entidad.getRol());
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("rut"),
                rs.getString("correo"),
                rs.getString("contraseña"),
                rs.getString("rol"));
    }
}
