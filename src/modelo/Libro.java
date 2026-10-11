package modelo;

/**
 * Representa un libro del catálogo, su categoría y las unidades disponibles.
 */
public class Libro {
    private int id;
    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private Integer stock;
    private Integer idCategoria;

    /**
     * Crea una instancia de Libro.
     */
    public Libro() {
    }

    /**
     * Crea una instancia de Libro con los datos indicados.
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param editorial editorial del libro
     * @param stock cantidad de unidades disponibles
     * @param idCategoria identificador de la categoría
     */
    public Libro(String titulo, String autor, String isbn, String editorial, Integer stock, Integer idCategoria) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    /**
     * Crea una instancia de Libro con los datos indicados.
     * @param id identificador del registro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param isbn ISBN del libro
     * @param editorial editorial del libro
     * @param stock cantidad de unidades disponibles
     * @param idCategoria identificador de la categoría
     */
    public Libro(int id, String titulo, String autor, String isbn, String editorial, Integer stock, Integer idCategoria) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.editorial = editorial;
        this.stock = stock;
        this.idCategoria = idCategoria;
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
     * Obtiene el título.
     * @return el título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título.
     * @param titulo título del libro
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene el autor.
     * @return el autor
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Establece el autor.
     * @param autor autor del libro
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * Obtiene el ISBN.
     * @return el ISBN
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Establece el ISBN.
     * @param isbn ISBN del libro
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Obtiene la editorial.
     * @return la editorial
     */
    public String getEditorial() {
        return editorial;
    }

    /**
     * Establece la editorial.
     * @param editorial editorial del libro
     */
    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    /**
     * Obtiene las unidades disponibles.
     * @return las unidades disponibles
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * Establece las unidades disponibles.
     * @param stock cantidad de unidades disponibles
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }

    /**
     * Obtiene el identificador de la categoría.
     * @return el identificador de la categoría
     */
    public Integer getIdCategoria() {
        return idCategoria;
    }

    /**
     * Establece el identificador de la categoría.
     * @param idCategoria identificador de la categoría
     */
    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
    /**
     * Proporciona el texto utilizado al mostrar el objeto en componentes Swing.
     * @return texto del objeto para su presentación en la interfaz
     */
    @Override
    public String toString() {
        return titulo + " (stock: " + stock + ")";
    }
}
