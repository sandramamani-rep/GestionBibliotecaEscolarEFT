package app;

import controlador.ControladorPrestamos;
import modelo.Usuario;
import java.time.LocalDate;

/**
 * Demostración manual de dos solicitudes concurrentes de préstamo. Ejecutarla escribe en la base de datos: deben configurarse identificadores de prueba existentes y comprobar el stock resultante.
 */
public class PruebaSincronizacion {

    /**
     * Ejecuta dos solicitudes concurrentes y espera a que ambos hilos terminen. Modifica registros reales de prueba.
     * @param args argumentos de ejecución, no utilizados
     * @throws InterruptedException si se interrumpe la espera de los hilos
     */
    public static void main(String[] args)
            throws InterruptedException {

        // Reemplaza estos valores por IDs existentes.
        final int idEstudiante = 1;
        final int idLibro = 1;

        // Contexto exclusivo de esta prueba local; no es un inicio de sesión.
        final Usuario usuarioPrueba = new Usuario();
        usuarioPrueba.setRol("bibliotecario");

        Runnable tarea = new Runnable() {
            @Override
            public void run() {
                // Cada hilo utiliza un controlador y un DAO distintos.
                ControladorPrestamos controlador =
                        new ControladorPrestamos(usuarioPrueba, null);

                boolean resultado = controlador.registrarPrestamo(
                        idEstudiante,
                        idLibro,
                        LocalDate.now()
                );

                System.out.println(
                        Thread.currentThread().getName()
                                + " terminó. Registrado: " + resultado
                );
            }
        };

        Thread hilo1 = new Thread(tarea, "Hilo 1");
        Thread hilo2 = new Thread(tarea, "Hilo 2");

        hilo1.start();
        hilo2.start();

        // Esperar a que ambos terminen antes de cerrar la prueba.
        hilo1.join();
        hilo2.join();

        System.out.println("Prueba finalizada.");
    }
}