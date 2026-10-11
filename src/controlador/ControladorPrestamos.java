package controlador;

import dao.PrestamoDAO;
import dao.impl.PrestamoDAOImpl;
import modelo.Prestamo;
import modelo.Usuario;
import modelo.Estudiante;
import java.time.LocalDate;
import java.util.List;

/**
 * Aplica las reglas de permisos y fechas de préstamos. El estudiante opera con su propio identificador; el bibliotecario consulta y gestiona los préstamos globales.
 */
public class ControladorPrestamos {
    private final PrestamoDAO prestamoDAO = new PrestamoDAOImpl();

    private final boolean bibliotecario;
    private final int idEstudianteActual;

    /**
     * Crea una instancia de ControladorPrestamos con los datos indicados.
     * @param usuario usuario autenticado
     * @param estudiante estudiante asociado; puede ser null para bibliotecarios
     * @throws IllegalArgumentException si falta el usuario o la asociación del estudiante no es válida
     */
    public ControladorPrestamos(Usuario usuario, Estudiante estudiante) {
        if (usuario == null) throw new IllegalArgumentException("Falta el usuario conectado.");
        bibliotecario = "bibliotecario".equals(usuario.getRol());
        if (bibliotecario) {
            idEstudianteActual = 0;
        } else if ("estudiante".equals(usuario.getRol()) && estudiante != null
                && estudiante.getId() > 0 && usuario.getRut() != null
                && usuario.getRut().equals(estudiante.getRut())) {
            idEstudianteActual = estudiante.getId();
        } else {
            throw new IllegalArgumentException("El usuario no tiene un estudiante asociado válido.");
        }
    }

    /**
     * Registra un préstamo con vencimiento a siete días. Para el rol estudiante utiliza su ID asociado y la fecha actual, independientemente de los argumentos recibidos.
     * @param idEstudiante identificador del estudiante
     * @param idLibro identificador del libro
     * @param fechaPrestamo fecha de inicio del préstamo
     * @return true si el préstamo y el descuento de stock se confirmaron; false si se rechazó o falló
     */
    public boolean registrarPrestamo(int idEstudiante, int idLibro,
                                     LocalDate fechaPrestamo) {
        if (!bibliotecario) {
            idEstudiante = idEstudianteActual;
            fechaPrestamo = LocalDate.now();
        }
        if (idEstudiante <= 0 || idLibro <= 0 || !fechaValida(fechaPrestamo)) {
            return false;
        }
        // Mientras está pendiente se guarda el vencimiento calculado.
        Prestamo prestamo = new Prestamo(idEstudiante, idLibro, fechaPrestamo, calcularVencimiento(fechaPrestamo), false);
        return prestamoDAO.guardar(prestamo);
    }

    /**
     * Solicita la devolución aplicando las restricciones del usuario conectado.
     * @param idPrestamo identificador del préstamo
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public boolean devolverPrestamo(int idPrestamo) {
        if (idPrestamo <= 0) return false;
        if (bibliotecario) return prestamoDAO.devolver(idPrestamo);
        return prestamoDAO.devolverDeEstudiante(idPrestamo, idEstudianteActual);
    }

    /**
     * Consulta todos los préstamos para el bibliotecario o solo los propios para el estudiante.
     * @return préstamos visibles para el usuario conectado
     */
    public List<Prestamo> obtenerPrestamos() {
        if (bibliotecario) return prestamoDAO.listarTodos();
        return prestamoDAO.listarPorEstudiante(idEstudianteActual);
    }

    /**
     * Permite al bibliotecario corregir el inicio y recalcular el vencimiento de un préstamo pendiente.
     * @param id identificador del registro
     * @param fechaPrestamo fecha de inicio del préstamo
     * @return true si se actualizó el préstamo; false si no se pudo realizar
     */
    public boolean actualizar(int id, LocalDate fechaPrestamo) {
        if (!bibliotecario || id <= 0 || !fechaValida(fechaPrestamo)) return false;
        Prestamo prestamo = new Prestamo();
        prestamo.setId(id);
        prestamo.setFechaPrestamo(fechaPrestamo);
        prestamo.setFechaDevolucion(calcularVencimiento(fechaPrestamo));
        return prestamoDAO.actualizar(prestamo);
    }



    private boolean fechaValida(LocalDate inicio) {
        return inicio != null && !inicio.isAfter(LocalDate.now())
                && inicio.getYear() >= 1000 && inicio.getYear() <= 9999
                && Prestamo.calcularVencimiento(inicio).getYear() <= 9999;
    }
    /**
     * Valida la fecha de inicio y calcula un plazo fijo de siete días.
     * @param inicio fecha de inicio del préstamo
     * @return fecha límite de devolución
     * @throws IllegalArgumentException si la fecha no es válida o es futura
     */
    public LocalDate calcularVencimiento(LocalDate inicio) {
        if (!fechaValida(inicio)) {
            throw new IllegalArgumentException("La fecha debe ser válida y no puede ser futura.");
        }
        return Prestamo.calcularVencimiento(inicio);
    }

}
