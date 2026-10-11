package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administra la conexión JDBC con la base de datos MySQL de la biblioteca escolar.
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String USER = "root";
    private static final String PASSWORD = "admin123";

    private static DatabaseConnection conexion;

    // Evita crear objetivos ConexionBD desde otra clase.
    private DatabaseConnection() {}


    // Si todavia no existe una instancia, se crea. Si ya existe, se devuelve la misma.
    public static synchronized DatabaseConnection getInstance() {
        if (conexion == null) {
            conexion = new DatabaseConnection();
        }
        return conexion;

    }

    /**
     * Establece una conexión con la base de datos configurada.
     *
     * @return conexión JDBC abierta.
     * @throws SQLException si ocurre un error al conectarse a MySQL.
     */
    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
