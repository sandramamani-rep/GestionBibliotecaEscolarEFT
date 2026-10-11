package modelo;

import java.time.LocalDate;

/**
 * Representa un préstamo. Mientras está pendiente, fechaDevolucion contiene el vencimiento; al devolverlo contiene la entrega real. El vencimiento se reconstruye con fechaPrestamo más siete días.
 */
public class Prestamo {
    /**
     * Plazo fijo del préstamo, expresado en días.
     */
    public static final int DIAS_PRESTAMO = 7;

    private int id;
    private Integer idEstudiante;
    private Integer idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private Boolean devuelto = false;

    /**
     * Crea una instancia de Prestamo.
     */
    public Prestamo() {
    }

    /**
     * Crea una instancia de Prestamo con los datos indicados.
     * @param idEstudiante identificador del estudiante
     * @param idLibro identificador del libro
     * @param fechaPrestamo fecha de inicio del préstamo
     * @param fechaDevolucion vencimiento si está pendiente, o entrega real si ya está devuelto
     * @param devuelto indica si se registró la entrega
     */
    public Prestamo(Integer idEstudiante, Integer idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, Boolean devuelto) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    /**
     * Crea una instancia de Prestamo con los datos indicados.
     * @param id identificador del registro
     * @param idEstudiante identificador del estudiante
     * @param idLibro identificador del libro
     * @param fechaPrestamo fecha de inicio del préstamo
     * @param fechaDevolucion vencimiento si está pendiente, o entrega real si ya está devuelto
     * @param devuelto indica si se registró la entrega
     */
    public Prestamo(int id, Integer idEstudiante, Integer idLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion, Boolean devuelto) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
        this.devuelto = devuelto;
    }

    /**
     * Obtiene el identificador.
     * @return el identificador
     */
    public int getId() {
        return id;
    }

    /**
     * Establece el identificador.
     * @param id identificador del registro
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtiene el identificador del estudiante.
     * @return el identificador del estudiante
     */
    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    /**
     * Establece el identificador del estudiante.
     * @param idEstudiante identificador del estudiante
     */
    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    /**
     * Obtiene el identificador del libro.
     * @return el identificador del libro
     */
    public Integer getIdLibro() {
        return idLibro;
    }

    /**
     * Establece el identificador del libro.
     * @param idLibro identificador del libro
     */
    public void setIdLibro(Integer idLibro) {
        this.idLibro = idLibro;
    }

    /**
     * Obtiene la fecha de inicio del préstamo.
     * @return la fecha de inicio del préstamo
     */
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    /**
     * Establece la fecha de inicio del préstamo.
     * @param fechaPrestamo fecha de inicio del préstamo
     */
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    /**
     * Obtiene la fecha almacenada en el préstamo.
     * @return vencimiento mientras está pendiente, o fecha real de entrega cuando está devuelto
     */
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    /**
     * Establece fecha devolucion.
     * @param fechaDevolucion vencimiento si está pendiente, o entrega real si ya está devuelto
     */
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    /**
     * Obtiene el indicador de devolución.
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public Boolean getDevuelto() {
        return devuelto;
    }

    /**
     * Establece el indicador de devolución.
     * @param devuelto indica si se registró la entrega
     */
    public void setDevuelto(Boolean devuelto) {
        this.devuelto = devuelto;
    }
    /**
     * Calcula el vencimiento sumando el plazo fijo a la fecha de inicio.
     * @param inicio fecha de inicio del préstamo
     * @return fecha límite, o null si no hay fecha de inicio
     */
    public static LocalDate calcularVencimiento(LocalDate inicio) {
        return inicio == null ? null : inicio.plusDays(DIAS_PRESTAMO);
    }

    /**
     * Reconstruye el vencimiento a partir de la fecha de inicio y el plazo fijo.
     * @return fecha de inicio más siete días, o null si no hay inicio
     */
    public LocalDate getFechaVencimiento() {
        return calcularVencimiento(fechaPrestamo);
    }

    /**
     * Calcula el estado actual del préstamo según sus fechas.
     * @return estado calculado según las fechas y la devolución
     */
    public String getEstado() {
        return determinarEstado(fechaPrestamo, fechaDevolucion,
                Boolean.TRUE.equals(devuelto), LocalDate.now());
    }

    /**
     * Clasifica el préstamo por sus fechas y devolución. Entregar en el vencimiento cuenta como a tiempo; informa también fechas ausentes o incoherentes.
     * @param inicio fecha de inicio del préstamo
     * @param entrega fecha guardada; se compara como entrega real solo si devuelto es true
     * @param devuelto indica si se registró la entrega
     * @param hoy fecha de referencia para evaluar préstamos pendientes
     * @return estado del préstamo para la fecha de referencia
     */
    public static String determinarEstado(LocalDate inicio, LocalDate entrega,
                                          boolean devuelto, LocalDate hoy) {
        LocalDate vencimiento = calcularVencimiento(inicio);
        if (vencimiento == null) return "Sin fecha de préstamo";
        if (devuelto) {
            if (entrega == null) return "Devuelto sin fecha registrada";
            if (entrega.isBefore(inicio)) return "Fecha de entrega inválida";
            return entrega.isAfter(vencimiento)
                    ? "Devuelto con atraso" : "Devuelto a tiempo";
        }
        return hoy.isAfter(vencimiento) ? "Atrasado" : "Pendiente";
    }

}