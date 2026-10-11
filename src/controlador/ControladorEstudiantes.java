package controlador;

import dao.EstudianteDAO;
import dao.impl.EstudianteDAOImpl;
import modelo.Estudiante;
import java.util.List;

/**
 * Valida los datos de estudiantes y delega su persistencia en el DAO correspondiente.
 */
public class ControladorEstudiantes {
    private final EstudianteDAO estudianteDAO;

    /**
     * Crea una instancia de ControladorEstudiantes.
     */
    public ControladorEstudiantes() {
        estudianteDAO = new EstudianteDAOImpl();
    }

    /**
     * Valida los datos y registra estudiante.
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param curso curso del estudiante
     * @param correo correo electrónico
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public boolean registrarEstudiante(String nombre, String rut, String curso, String correo) {
        if (nombre == null
                || nombre.isBlank()
                || rut == null
                || rut.isBlank()
                || curso == null
                || curso.isBlank()
                || correo == null
                || correo.isBlank()) {
            return false;
        }
        Estudiante estudiante = new Estudiante(nombre.trim(), rut.trim(), curso.trim(), correo.trim());
        return estudianteDAO.guardar(estudiante);
    }

    /**
     * Consulta los registros de estudiantes.
     * @return lista de resultados, vacía si no hay coincidencias
     */
    public List<Estudiante> obtenerEstudiantes() {
        return estudianteDAO.listarTodos();
    }

    /**
     * Actualiza los datos del registro indicado.
     * @param id identificador del registro
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param curso curso del estudiante
     * @param correo correo electrónico
     * @return true si se modificó un registro; false si no se pudo actualizar
     */
    public boolean actualizar(int id, String nombre, String rut, String curso, String correo) {
        if (id <= 0) {
            return false;
        }
        if (nombre == null
                || nombre.isBlank()
                || rut == null
                || rut.isBlank()
                || curso == null
                || curso.isBlank()
                || correo == null
                || correo.isBlank()) {
            return false;
        }
        Estudiante estudiante = new Estudiante(id, nombre.trim(), rut.trim(), curso.trim(), correo.trim());
        return estudianteDAO.actualizar(estudiante);
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
        return estudianteDAO.eliminar(id);
    }
    /**
     * Obtiene estudiantes disponibles para autocompletar una cuenta nueva.
     * @return estudiantes cuyo RUT no existe en usuarios
     */
    public List<Estudiante> obtenerEstudiantesSinUsuario() {
        return estudianteDAO.listarSinUsuario();
    }

}
