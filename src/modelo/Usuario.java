package modelo;

/**
 * Representa una cuenta de acceso, con RUT, contraseña y rol.
 */
public class Usuario extends Persona {
    private String contrasenia;
    private String rol;

    /**
     * Crea una instancia de Usuario.
     */
    public Usuario() {
        super();
    }

    /**
     * Crea una instancia de Usuario con los datos indicados.
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param correo correo electrónico
     * @param contrasenia contraseña de la cuenta
     * @param rol rol de acceso: bibliotecario o estudiante
     */
    public Usuario(String nombre, String rut, String correo, String contrasenia, String rol) {
        super(nombre, rut, correo);
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    /**
     * Crea una instancia de Usuario con los datos indicados.
     * @param id identificador del registro
     * @param nombre nombre del registro
     * @param rut RUT con el formato almacenado en la base de datos
     * @param correo correo electrónico
     * @param contrasenia contraseña de la cuenta
     * @param rol rol de acceso: bibliotecario o estudiante
     */
    public Usuario(int id, String nombre, String rut, String correo, String contrasenia, String rol) {
        super(id, nombre, rut, correo);
        this.contrasenia = contrasenia;
        this.rol = rol;
    }

    /**
     * Obtiene la contraseña.
     * @return la contraseña
     */
    public String getContrasenia() { return contrasenia; }
    /**
     * Establece la contraseña.
     * @param contrasenia contraseña de la cuenta
     */
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }
    /**
     * Obtiene el rol.
     * @return el rol
     */
    public String getRol() { return rol; }
    /**
     * Establece el rol.
     * @param rol rol de acceso: bibliotecario o estudiante
     */
    public void setRol(String rol) { this.rol = rol; }

    /**
     * Construye la descripción específica de esta persona.
     * @return descripción propia del tipo de persona
     */
    @Override
    public String getDescripcion() {
        return getNombre() + " - " + rol;
    }
}
