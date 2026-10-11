package controlador;
import dao.CategoriaDAO;
import dao.impl.CategoriaDAOImpl;
import modelo.Categoria;
import java.util.List;

/**
 * Valida los datos de categorias y delega su persistencia en el DAO correspondiente.
 */
public class ControladorCategorias {
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();
    /**
     * Valida los datos y registra categoria.
     * @param nombre nombre del registro
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public boolean registrarCategoria(String nombre) {
        if (nombre == null || nombre.isBlank()) return false;
        return categoriaDAO.guardar(new Categoria(nombre.trim()));
    }
    /**
     * Consulta los registros de categorias.
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<Categoria> obtenerCategorias() { return categoriaDAO.listarTodos(); }
    /**
     * Actualiza los datos del registro indicado.
     * @param id identificador del registro
     * @param nombre nombre del registro
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    public boolean actualizar(int id, String nombre) {
        if (id <= 0 || nombre == null || nombre.isBlank()) return false;
        return categoriaDAO.actualizar(new Categoria(id, nombre.trim()));
    }
    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    public boolean eliminar(int id) {
        if (id <= 0) return false;
        return categoriaDAO.eliminar(id);
    }
}
