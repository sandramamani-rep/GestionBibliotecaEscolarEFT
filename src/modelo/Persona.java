package modelo;

/**
 * Define los datos comunes de usuarios y estudiantes. Sus subclases implementan la descripción utilizada por los componentes Swing.
 */
public abstract class Persona {
    private int id;
    private String nombre;
    private String rut;
    private String correo;

    /**
     * Crea una instancia de Persona.
     */
    protected Persona() {
    }

    /**
     * Crea una instancia de Persona con los datos indicados.
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param correo correo electrónico
     */
    protected Persona(String nombre, String rut, String correo) {
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    /**
     * Crea una instancia de Persona con los datos indicados.
     * @param id identificador del registro
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param correo correo electrónico
     */
    protected Persona(int id, String nombre, String rut, String correo) {
        this(nombre, rut, correo);
        this.id = id;
    }

    /**
     * Obtiene el identificador.
     * @return el identificador
     */
    public int getId() { return id; }
    /**
     * Establece el identificador.
     * @param id identificador del registro
     */
    public void setId(int id) { this.id = id; }
    /**
     * Obtiene el nombre.
     * @return el nombre
     */
    public String getNombre() { return nombre; }
    /**
     * Establece el nombre.
     * @param nombre nombre del registro
     */
    public void setNombre(String nombre) { this.nombre = nombre; }
    /**
     * Obtiene el RUT.
     * @return el RUT
     */
    public String getRut() { return rut; }
    /**
     * Establece el RUT.
     * @param rut RUT con el formato almacenado en la base de datos
     */
    public void setRut(String rut) { this.rut = rut; }
    /**
     * Obtiene el correo electrónico.
     * @return el correo electrónico
     */
    public String getCorreo() { return correo; }
    /**
     * Establece el correo electrónico.
     * @param correo correo electrónico
     */
    public void setCorreo(String correo) { this.correo = correo; }

    /**
     * Construye la descripción específica de esta persona.
     * @return descripción propia del tipo de persona
     */
    public abstract String getDescripcion();

    /**
     * Proporciona el texto utilizado al mostrar el objeto en componentes Swing.
     * @return texto del objeto para su presentación en la interfaz
     */
    @Override
    public String toString() {
        return getDescripcion();
    }
}
