package vista;

import controlador.*;
import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Organiza las vistas en pestañas, aplica los permisos por rol y controla la navegación durante los procesos de préstamos.
 */
public class VentanaBiblioteca extends JFrame {
    private JTabbedPane tabbedPane1;
    private JPanel VentanaBiblioteca;

    private VistaReportes vistaReportes;
    private VistaUsuarios vistaUsuarios;
    private VistaCategorias vistaCategorias;
    private VistaEstudiantes vistaEstudiantes;
    private VistaPrestamos vistaPrestamos;
    private VistaLibros vistaLibros;
    private final Usuario usuarioActual;

    private final JButton btnActualizarListas = new JButton("Actualizar listas");
    private final JButton btnCerrar = new JButton("Cerrar");

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     * @param usuario usuario autenticado
     * @param estudiante estudiante asociado; puede ser null para bibliotecarios
     */
    public VentanaBiblioteca(Usuario usuario, Estudiante estudiante) {
        usuarioActual = usuario;
        setTitle("Biblioteca Escolar — " + usuario.getNombre() + " (" + usuario.getRol() + ")");
        JPanel contenido = new JPanel(new BorderLayout(8, 8));
        contenido.add(VentanaBiblioteca, BorderLayout.CENTER);
        JPanel acciones = new JPanel();
        acciones.add(btnActualizarListas);
        acciones.add(btnCerrar);
        contenido.add(acciones, BorderLayout.SOUTH);
        setContentPane(contenido);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 650);
        setLocationRelativeTo(null);

        ControladorLibros libros = new ControladorLibros();
        ControladorCategorias categorias = new ControladorCategorias();
        ControladorEstudiantes estudiantes = new ControladorEstudiantes();
        ControladorPrestamos prestamos = new ControladorPrestamos(usuario, estudiante);

        if (esBibliotecario()) {
            vistaUsuarios = new VistaUsuarios(new ControladorUsuarios(), usuario);
            vistaEstudiantes = new VistaEstudiantes(estudiantes, usuario);
            vistaCategorias = new VistaCategorias(categorias, usuario);
            colocarVista("Usuarios", vistaUsuarios);
            colocarVista("Estudiantes", vistaEstudiantes);
            colocarVista("Categorias", vistaCategorias);
            vistaReportes = new VistaReportes(new ControladorReportes(), estudiantes, usuario);
            colocarVista("Informes", vistaReportes);
        }
        vistaLibros = new VistaLibros(libros, categorias, usuario);
        colocarVista("Libros", vistaLibros);
        vistaPrestamos = new VistaPrestamos(prestamos, libros, estudiantes,
                usuario, estudiante, this);
        colocarVista("Prestamos", vistaPrestamos);
        aplicarPermisos();
        btnActualizarListas.addActionListener(e -> refrescar());
        tabbedPane1.addChangeListener(e -> {
            if (isShowing() && tabbedPane1.isEnabled()) refrescar();
        });
        btnCerrar.addActionListener(e -> {

                System.exit(0);
        });
    }

    private void colocarVista(String titulo, JPanel vista) {
        int indice = tabbedPane1.indexOfTab(titulo);
        if (indice < 0) throw new IllegalStateException("No existe la pestaña " + titulo);
        tabbedPane1.setComponentAt(indice, vista);
    }

    private boolean esBibliotecario() {
        return "bibliotecario".equals(usuarioActual.getRol());
    }

    /**
     * Aplica los permisos del usuario conectado a los controles o pestañas de esta vista.
     */
    private void aplicarPermisos() {
        if (!esBibliotecario()) {
            for (int i = 0; i < tabbedPane1.getTabCount(); i++) {
                Component vista = tabbedPane1.getComponentAt(i);
                tabbedPane1.setEnabledAt(i, vista == vistaLibros || vista == vistaPrestamos);
            }
            tabbedPane1.setSelectedComponent(vistaLibros);
        }
    }

    private void refrescar() {
        Component vista = tabbedPane1.getSelectedComponent();
        if (vista == vistaUsuarios && vistaUsuarios != null) vistaUsuarios.refrescar();
        else if (vista == vistaEstudiantes && vistaEstudiantes != null) vistaEstudiantes.refrescar();
        else if (vista == vistaCategorias && vistaCategorias != null) vistaCategorias.refrescar();
        else if (vista == vistaLibros) vistaLibros.refrescar();
        else if (vista == vistaPrestamos) vistaPrestamos.refrescar();
        else if (vista == vistaReportes && vistaReportes != null) vistaReportes.refrescar();
    }

    /**
     * Habilita o bloquea pestañas y actualización de listas. Debe llamarse desde el hilo de eventos de Swing.
     * @param habilitar true para permitir la navegación; false para bloquearla durante una operación
     */
    public void habilitarNavegacion(boolean habilitar) {
        tabbedPane1.setEnabled(habilitar);
        btnActualizarListas.setEnabled(habilitar);
    }

}
