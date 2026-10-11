package modelo;

/**
 * Representa un libro y la cantidad de préstamos registrada para el reporte de frecuencia.
 */
public class ReporteLibro {
    private final int idLibro;
    private final String titulo;
    private final String autor;
    private final String isbn;
    private final long cantidadPrestamos;

    /**
     * Crea una instancia de ReporteLibro con los datos indicados.
     * @param idLibro identificador del libro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param cantidadPrestamos cantidad de préstamos contabilizados
     */
    public ReporteLibro(int idLibro, String titulo, String autor, String isbn, long cantidadPrestamos) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.cantidadPrestamos = cantidadPrestamos;
    }

    /**
     * Obtiene el identificador del libro.
     * @return el identificador del libro
     */
    public int getIdLibro() { return idLibro; }

    /**
     * Obtiene el título.
     * @return el título
     */
    public String getTitulo() { return titulo; }

    /**
     * Obtiene el autor.
     * @return el autor
     */
    public String getAutor() { return autor; }

    /**
     * Obtiene el ISBN.
     * @return el ISBN
     */
    public String getIsbn() { return isbn; }

    /**
     * Obtiene la cantidad de préstamos contabilizados.
     * @return la cantidad de préstamos contabilizados
     */
    public long getCantidadPrestamos() { return cantidadPrestamos; }
}
