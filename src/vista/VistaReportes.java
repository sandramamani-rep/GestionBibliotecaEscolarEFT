package vista;

import controlador.ControladorReportes;
import controlador.ControladorEstudiantes;
import modelo.Estudiante;
import modelo.ReporteLibro;
import modelo.ReportePrestamo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import modelo.Usuario;
import java.util.List;

/**
 * Presenta los reportes de frecuencia de libros, historial por estudiante y préstamos pendientes. Las consultas se ejecutan directamente desde la interfaz.
 */
public class VistaReportes extends JPanel {
    private JPanel jpReportes;
    private JComboBox<String> cmbReporte;
    private JComboBox<Estudiante> cmbEstudiante;
    private JButton btnConsultar;
    private JButton btnActualizarEstudiantes;
    private JTable tabla;
    private JLabel lblResultado;
    private final ControladorReportes controladorReportes;
    private final ControladorEstudiantes controladorEstudiantes;

    /**
     * Crea la vista de informes para el bibliotecario.
     * @param reportes controlador de consultas de reportes
     * @param estudiantes controlador del selector de estudiantes
     * @param usuario usuario autenticado
     * @throws IllegalArgumentException si no tiene rol bibliotecario
     */
    public VistaReportes(ControladorReportes reportes,
                         ControladorEstudiantes estudiantes, Usuario usuario) {
        if (usuario == null || !"bibliotecario".equals(usuario.getRol())) {
            throw new IllegalArgumentException("Acceso exclusivo del bibliotecario.");
        }
        controladorReportes = reportes;
        controladorEstudiantes = estudiantes;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jpReportes, BorderLayout.CENTER);
        cmbReporte.addItem("Libros más prestados");
        cmbReporte.addItem("Historial por estudiante");
        cmbReporte.addItem("Préstamos pendientes");
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        limpiarTabla();
        cmbEstudiante.setEnabled(false);
        btnActualizarEstudiantes.setEnabled(false);
        configurarEventos();
    }

    /** Conecta los controles con las consultas y los filtros de la vista. */
    private void configurarEventos() {
        btnConsultar.addActionListener(e -> consultar());
        btnActualizarEstudiantes.addActionListener(e -> cargarEstudiantes());
        cmbReporte.addActionListener(e -> cambiarReporte());
        cmbEstudiante.addActionListener(e -> {
            if (cmbReporte.getSelectedIndex() == 1) {
                limpiarTabla();
                lblResultado.setText("Pulsa Consultar para ver el historial del estudiante seleccionado.");
            }
        });
    }

    /**
     * Actualiza el reporte activo. En historial recarga el selector y permite elegir estudiante.
     * Debe llamarse desde el hilo de eventos de Swing.
     */
    public void refrescar() {
        if (cmbReporte.getSelectedIndex() == 1) cargarEstudiantes();
        else consultar();
    }

    private void cambiarReporte() {
        limpiarTabla();
        boolean historial = cmbReporte.getSelectedIndex() == 1;
        cmbEstudiante.setEnabled(historial);
        btnActualizarEstudiantes.setEnabled(historial);
        lblResultado.setText("Pulsa Consultar para generar el reporte.");
        if (historial) cargarEstudiantes();
    }

    private void cargarEstudiantes() {
        limpiarTabla();
        cmbEstudiante.removeAllItems();
        try {
            List<Estudiante> estudiantes = controladorEstudiantes.obtenerEstudiantes();
            for (Estudiante estudiante : estudiantes) cmbEstudiante.addItem(estudiante);
            cmbEstudiante.setSelectedIndex(-1);
            if (estudiantes.isEmpty()) {
                lblResultado.setText("No hay estudiantes registrados.");
            } else {
                lblResultado.setText("Selecciona un estudiante y pulsa Consultar.");
            }
        } catch (IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    private void consultar() {
        // Se limpia el resultado anterior para no mostrarlo como si fuera la consulta nueva.
        limpiarTabla();
        try {
            int tipo = cmbReporte.getSelectedIndex();
            if (tipo == 0) {
                mostrarLibros(controladorReportes.obtenerLibrosMasPrestados());
            } else if (tipo == 1) {
                Estudiante estudiante = (Estudiante) cmbEstudiante.getSelectedItem();
                if (estudiante == null) {
                    lblResultado.setText("Selecciona un estudiante.");
                    JOptionPane.showMessageDialog(this, "Selecciona un estudiante para consultar su historial.");
                    return;
                }
                mostrarPrestamos(controladorReportes.obtenerHistorial(estudiante.getId()));
            } else {
                mostrarPrestamos(controladorReportes.obtenerPrestamosPendientes());
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarTabla() {
        if (cmbReporte.getSelectedIndex() == 0) {
            tabla.setModel(new DefaultTableModel(new String[]{"ID libro", "Título", "Autor", "ISBN", "Veces prestado"}, 0) {
                @Override public boolean isCellEditable(int fila, int columna) { return false; }
            });
        } else {
            tabla.setModel(new DefaultTableModel(new String[]{"ID préstamo", "Estudiante", "RUT", "Libro", "Fecha préstamo", "Vencimiento", "Entrega real", "Estado"}, 0) {
                @Override public boolean isCellEditable(int fila, int columna) { return false; }
            });
        }
    }

    private void mostrarLibros(List<ReporteLibro> libros) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        for (ReporteLibro libro : libros) {
            modelo.addRow(new Object[]{libro.getIdLibro(), libro.getTitulo(), libro.getAutor(),
                    libro.getIsbn(), libro.getCantidadPrestamos()});
        }
        if (libros.isEmpty()) {
            lblResultado.setText("No hay préstamos registrados para generar el ranking.");
        } else {
            lblResultado.setText("Libros con préstamos: " + libros.size() + ". Incluye registros pendientes y devueltos.");
        }
    }

    private void mostrarPrestamos(List<ReportePrestamo> prestamos) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        for (ReportePrestamo prestamo : prestamos) {
            modelo.addRow(new Object[]{prestamo.getIdPrestamo(), prestamo.getEstudiante(), prestamo.getRut(),
                    prestamo.getLibro(), prestamo.getFechaPrestamo(), prestamo.getFechaVencimiento(), prestamo.getFechaEntrega(), prestamo.getEstado()});
        }
        if (prestamos.isEmpty()) {
            lblResultado.setText("No hay préstamos para este reporte.");
        } else {
            lblResultado.setText("Préstamos encontrados: " + prestamos.size());
        }
    }

    private void mostrarError(String mensaje) {
        lblResultado.setText("No se pudo generar el reporte. Revisa la conexión y vuelve a consultar.");
        JOptionPane.showMessageDialog(this, mensaje, "Error de reportes", JOptionPane.ERROR_MESSAGE);
    }
}
