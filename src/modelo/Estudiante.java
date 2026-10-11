package modelo;

/**
 * Representa un estudiante y su curso. El login relaciona esta entidad con una cuenta mediante el RUT.
 */
public class Estudiante extends Persona {
    private String curso;

    /**
     * Crea una instancia de Estudiante.
     */
    public Estudiante() {
        super();
    }

    /**
     * Crea una instancia de Estudiante con los datos indicados.
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param curso curso del estudiante
     * @param correo correo electrónico
     */
    public Estudiante(String nombre, String rut, String curso, String correo) {
        super(nombre, rut, correo);
        this.curso = curso;
    }

    /**
     * Crea una instancia de Estudiante con los datos indicados.
     * @param id identificador del registro
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param curso curso del estudiante
     * @param correo correo electrónico
     */
    public Estudiante(int id, String nombre, String rut, String curso, String correo) {
        super(id, nombre, rut, correo);
        this.curso = curso;
    }

    /**
     * Obtiene el curso.
     * @return el curso
     */
    public String getCurso() { return curso; }
    /**
     * Establece el curso.
     * @param curso curso del estudiante
     */
    public void setCurso(String curso) { this.curso = curso; }

    /**
     * Construye la descripción específica de esta persona.
     * @return descripción propia del tipo de persona
     */
    @Override
    public String getDescripcion() {
        return getNombre() + " - " + getRut();
    }
}
