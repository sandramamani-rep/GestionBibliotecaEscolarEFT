package app;

import vista.Login;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada que abre el login en el hilo de eventos de Swing.
 */
public class Main {
    /**
     * Inicia la ejecución de la aplicación.
     * @param args argumentos de ejecución, no utilizados
     */
    public static void main(String[] args) {
        // Crea la interfaz en el hilo de eventos de Swing.
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}
