package modelo;

/**
 * Representa una fila de historial o de préstamos pendientes. Interpreta la fecha guardada como entrega real únicamente cuando el préstamo está devuelto.
 */
public class ReportePrestamo {
    private final int idPrestamo;
    private final String estudiante;
    private final String rut;
    private final String libro;
    private final java.time.LocalDate fechaPrestamo;
    private final java.time.LocalDate fechaEntrega;
    private final boolean devuelto;

    /**
     * Crea una instancia de ReportePrestamo con los datos indicados.
     * @param idPrestamo identificador del préstamo
     * @param estudiante nombre del estudiante mostrado en el reporte
     * @param rut RUT con el formato almacenado en la base de datos
     * @param libro título del libro mostrado en el reporte
     * @param fechaPrestamo fecha de inicio del préstamo
     * @param fechaEntrega fecha de la base de datos, interpretada como entrega real solo si devuelto es true
     * @param devuelto indica si se registró la entrega
     */
    public ReportePrestamo(int idPrestamo, String estudiante, String rut, String libro, java.time.LocalDate fechaPrestamo, java.time.LocalDate fechaEntrega, boolean devuelto) {
        this.idPrestamo = idPrestamo;
        this.estudiante = estudiante;
        this.rut = rut;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaEntrega = fechaEntrega;
        this.devuelto = devuelto;
    }

    /**
     * Obtiene el identificador del préstamo.
     * @return el identificador del préstamo
     */
    public int getIdPrestamo() { return idPrestamo; }

    /**
     * Obtiene el nombre del estudiante.
     * @return el nombre del estudiante
     */
    public String getEstudiante() { return estudiante; }

    /**
     * Obtiene el RUT.
     * @return el RUT
     */
    public String getRut() { return rut; }

    /**
     * Obtiene el título del libro.
     * @return el título del libro
     */
    public String getLibro() { return libro; }

    /**
     * Obtiene la fecha de inicio del préstamo.
     * @return la fecha de inicio del préstamo
     */
    public java.time.LocalDate getFechaPrestamo() { return fechaPrestamo; }

    /**
     * Obtiene la entrega real únicamente para un préstamo devuelto.
     * @return fecha de entrega, o null si está pendiente o no tiene fecha registrada
     */
    public java.time.LocalDate getFechaEntrega() {
        return devuelto ? fechaEntrega : null;
    }

    /**
     * Reconstruye el vencimiento a partir de la fecha de inicio y el plazo fijo.
     * @return fecha de inicio más siete días, o null si no hay inicio
     */
    public java.time.LocalDate getFechaVencimiento() {
        return Prestamo.calcularVencimiento(fechaPrestamo);
    }

    /**
     * Indica si se registró la devolución.
     * @return true si se cumple la condición o se completa la operación; false en otro caso
     */
    public boolean isDevuelto() { return devuelto; }

    /**
     * Calcula el estado actual del préstamo según sus fechas.
     * @return estado calculado según las fechas y la devolución
     */
    public String getEstado() {
        return Prestamo.determinarEstado(fechaPrestamo, fechaEntrega,
                devuelto, java.time.LocalDate.now());
    }
}
