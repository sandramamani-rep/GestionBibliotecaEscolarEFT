package controlador;

import dao.LibroDAO;
import dao.impl.LibroDAOImpl;
import modelo.Libro;
import java.util.List;

/**
 * Valida los datos de libros y delega su persistencia en el DAO correspondiente.
 */
public class ControladorLibros {
    private final LibroDAO libroDAO;

    /**
     * Crea una instancia de ControladorLibros.
     */
    public ControladorLibros() {
        libroDAO = new LibroDAOImpl();
    }

    /**
     * Valida los datos y registra libro.
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param editorial editorial del libro
     * @param stock cantidad de unidades disponibles
     * @param idCategoria identificador de la categoría
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public boolean registrarLibro(String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        if (titulo == null
                || titulo.isBlank()
                || autor == null
                || autor.isBlank()
                || isbn == null
                || isbn.isBlank()
                || editorial == null
                || editorial.isBlank()
                || stock < 0
                || idCategoria <= 0) {
            return false;
        }
        Libro libro = new Libro(titulo.trim(), autor.trim(), isbn.trim(), editorial.trim(), stock, idCategoria);
        return libroDAO.guardar(libro);
    }

    /**
     * Consulta los registros de libros.
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<Libro> obtenerLibros() {
        return libroDAO.listarTodos();
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param id identificador del registro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param editorial editorial del libro
     * @param stock cantidad de unidades disponibles
     * @param idCategoria identificador de la categoría
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    public boolean actualizar(int id, String titulo, String autor, String isbn, String editorial, int stock, int idCategoria) {
        if (id <= 0) {
            return false;
        }
        if (titulo == null
                || titulo.isBlank()
                || autor == null
                || autor.isBlank()
                || isbn == null
                || isbn.isBlank()
                || editorial == null
                || editorial.isBlank()
                || stock < 0
                || idCategoria <= 0) {
            return false;
        }
        Libro libro = new Libro(id, titulo.trim(), autor.trim(), isbn.trim(), editorial.trim(), stock, idCategoria);
        return libroDAO.actualizar(libro);
    }

    /**
     * Elimina el registro indicado, sujeto a las restricciones de la base de datos.
     * @param id identificador del registro
     * @return true si se eliminó el registro; false si no se pudo eliminar
     */
    public boolean eliminar(int id) {
        if (id <= 0) {
            return false;
        }
        return libroDAO.eliminar(id);
    }
}
