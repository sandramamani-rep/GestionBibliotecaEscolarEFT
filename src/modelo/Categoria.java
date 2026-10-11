package modelo;

/**
 * Representa una categoría del catálogo de libros.
 */
public class Categoria {
    private int id;
    private String nombre;

    /**
     * Crea una instancia de Categoria.
     */
    public Categoria() {
    }

    /**
     * Crea una instancia de Categoria con los datos indicados.
     * @param nombre nombre del registro
     */
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Crea una instancia de Categoria con los datos indicados.
     * @param id identificador del registro
     * @param nombre nombre del registro
     */
    public Categoria(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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
     * Obtiene el nombre.
     * @return el nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre.
     * @param nombre nombre del registro
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * Proporciona el texto utilizado al mostrar el objeto en componentes Swing.
     * @return texto del objeto para su presentación en la interfaz
     */
    @Override
    public String toString() {
        return nombre;
    }
}
