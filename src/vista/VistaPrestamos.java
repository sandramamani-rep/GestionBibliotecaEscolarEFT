package vista;

import controlador.*;
import modelo.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenta préstamos e historial según el usuario conectado. Las operaciones y recargas JDBC se ejecutan con SwingWorker y los componentes se actualizan en el hilo de Swing.
 */
public class VistaPrestamos extends JPanel {
    private JLabel lblEstudiante;
    private JLabel lblLibro;
    private JLabel lblFechaPrestamo;
    private JLabel lblFechaDevolucion;
    private JComboBox cmbEstudiantes;
    private JComboBox cmbLibros;
    private JTable tblPrestamos;
    private JTextField txtFechaPrestamo;
    private JTextField txtFechaDevolucion;
    private JButton btnAgregarPrestamo;
    private JButton btnEditarPrestamo;
    private JButton btnEliminarPrestamo;
    private JButton btnLimpiarPrestamo;
    private JPanel jpPrestamos;

    private final Usuario usuarioActual;
    private final Estudiante estudianteActual;
    private final ControladorPrestamos prestamos;
    private final ControladorLibros controladorLibros;
    private final ControladorEstudiantes controladorEstudiantes;
    private final VentanaBiblioteca ventana;
    private final JCheckBox chkDevuelto = new JCheckBox("Préstamo devuelto");
    private final JButton btnDevolverPrestamo = new JButton("Devolver préstamo");
    private DefaultTableModel modeloTablaPrestamos;
    private int idPrestamoSeleccionado = -1;
    private List<Estudiante> listaEstudiantes = new ArrayList<>();
    private List<Libro> listaLibros = new ArrayList<>();
    private List<Prestamo> listaPrestamos = new ArrayList<>();

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     * @param prestamos controlador de préstamos con el contexto de la sesión
     * @param libros controlador de libros
     * @param estudiantes controlador de estudiantes
     * @param usuario usuario autenticado
     * @param estudiante estudiante asociado; puede ser null para bibliotecarios
     * @param ventana ventana principal que controla la navegación
     */
    public VistaPrestamos(ControladorPrestamos prestamos, ControladorLibros libros,
                          ControladorEstudiantes estudiantes, Usuario usuario,
                          Estudiante estudiante, VentanaBiblioteca ventana) {
        this.prestamos = prestamos;
        this.controladorLibros = libros;
        this.controladorEstudiantes = estudiantes;
        this.usuarioActual = usuario;
        this.estudianteActual = estudiante;
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jpPrestamos, BorderLayout.CENTER);
        JPanel acciones = new JPanel();
        acciones.add(chkDevuelto);
        acciones.add(btnDevolverPrestamo);
        add(acciones, BorderLayout.SOUTH);
        inicializarTablaPrestamos();
        txtFechaPrestamo.setToolTipText("Formato AAAA-MM-DD");
        lblFechaDevolucion.setText("Vencimiento automático:");
        txtFechaDevolucion.setToolTipText("Fecha de préstamo más 7 días");
        txtFechaDevolucion.setEditable(false);
        txtFechaPrestamo.setEditable(esBibliotecario());
        btnEliminarPrestamo.setEnabled(false);
        btnEliminarPrestamo.setVisible(false);
        txtFechaPrestamo.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarVencimiento(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarVencimiento(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarVencimiento(); }
        });
        btnAgregarPrestamo.addActionListener(e -> agregarPrestamo());
        btnEditarPrestamo.addActionListener(e -> editarPrestamo());
        btnLimpiarPrestamo.addActionListener(e -> limpiarPrestamo());
        btnDevolverPrestamo.addActionListener(e -> devolverPrestamo());
        limpiarPrestamo();
        habilitarControlesPrestamos(true);
    }

    /**
     * Solicita una recarga en segundo plano si no hay otra operación en curso. Debe llamarse desde el hilo de eventos de Swing.
     */
    public void refrescar() {
        // El botón está deshabilitado mientras corre una operación o recarga.
        if (!btnAgregarPrestamo.isEnabled()) return;
        cargarDatosPrestamosEnSegundoPlano();
    }

    private void cargarCombosPrestamos() {
        cmbEstudiantes.removeAllItems();
        for (Estudiante estudiante : listaEstudiantes) {
            cmbEstudiantes.addItem(estudiante);
        }
        cmbLibros.removeAllItems();
        for (Libro libro : listaLibros) cmbLibros.addItem(libro);
        if (esBibliotecario()) cmbEstudiantes.setSelectedIndex(-1);
        else cmbEstudiantes.setSelectedItem(estudianteActual);
        cmbEstudiantes.setEnabled(esBibliotecario());
        cmbLibros.setSelectedIndex(-1);
    }

    private void inicializarTablaPrestamos() {
        modeloTablaPrestamos = new DefaultTableModel(new String[]{"ID", "Estudiante", "Libro", "Préstamo", "Vencimiento", "Entrega real", "Estado"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tblPrestamos.setModel(modeloTablaPrestamos);
        tblPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarPrestamo();
        });
    }

    private void seleccionarPrestamo() {
        int fila = tblPrestamos.getSelectedRow();
        if (fila < 0) {
            idPrestamoSeleccionado = -1;
            actualizarBotonesSeleccion();
            return;
        }
        Prestamo p = listaPrestamos.get(tblPrestamos.convertRowIndexToModel(fila));
        idPrestamoSeleccionado = p.getId();
        cmbEstudiantes.setSelectedIndex(-1);
        for (int i = 0; i < cmbEstudiantes.getItemCount(); i++) {
            Estudiante estudiante = (Estudiante) cmbEstudiantes.getItemAt(i);
            if (p.getIdEstudiante() != null && estudiante.getId() == p.getIdEstudiante()) cmbEstudiantes.setSelectedIndex(i);
        }
        cmbLibros.setSelectedIndex(-1);
        for (int i = 0; i < cmbLibros.getItemCount(); i++) {
            Libro libro = (Libro) cmbLibros.getItemAt(i);
            if (p.getIdLibro() != null && libro.getId() == p.getIdLibro()) cmbLibros.setSelectedIndex(i);
        }
        txtFechaPrestamo.setText(p.getFechaPrestamo() == null ? "" : p.getFechaPrestamo().toString());
        actualizarVencimiento();
        chkDevuelto.setSelected(Boolean.TRUE.equals(p.getDevuelto()));
        // Para otro préstamo pulsa Limpiar; Editar conserva estas relaciones.
        cmbEstudiantes.setEnabled(false);
        cmbLibros.setEnabled(false);
        actualizarBotonesSeleccion();
    }

    private void agregarPrestamo() {
        if (!btnAgregarPrestamo.isEnabled()) return;
        Estudiante estudiante = esBibliotecario()
                ? (Estudiante) cmbEstudiantes.getSelectedItem() : estudianteActual;
        Libro libro = (Libro) cmbLibros.getSelectedItem();
        if (estudiante == null || libro == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante y un libro.");
            return;
        }
        final LocalDate inicio;
        try {
            inicio = LocalDate.parse(txtFechaPrestamo.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Usa fechas válidas con formato AAAA-MM-DD.");
            return;
        }
        final int idEstudiante = estudiante.getId();
        final int idLibro = libro.getId();
        habilitarControlesPrestamos(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                // Este metodo corre fuera del hilo de la ventana.
                return prestamos.registrarPrestamo(idEstudiante, idLibro, inicio);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(VistaPrestamos.this,
                                "Préstamo registrado. Se descontó una unidad del stock.");
                    } else {
                        JOptionPane.showMessageDialog(VistaPrestamos.this,
                                "No se registró el préstamo. Revisa las fechas, el stock y la conexión.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(VistaPrestamos.this,
                            "No se pudo completar el registro del préstamo.");
                } finally {
                    // Las consultas para refrescar también van en segundo plano.
                    cargarDatosPrestamosEnSegundoPlano();
                }
            }
        };
        worker.execute();
    }

    private void devolverPrestamo() {
        if (!tblPrestamos.isEnabled()) return;
        Prestamo seleccionado = obtenerPrestamoSeleccionado();
        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona el préstamo que deseas devolver.");
            return;
        }
        if (Boolean.TRUE.equals(seleccionado.getDevuelto())) {
            JOptionPane.showMessageDialog(this, "Este préstamo ya fue devuelto.");
            actualizarBotonesSeleccion();
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Registrar la devolución del préstamo seleccionado?", "Confirmar devolución", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        final int id = seleccionado.getId();
        habilitarControlesPrestamos(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return prestamos.devolverPrestamo(id);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(VistaPrestamos.this,
                                "Devolución registrada. Se repuso una unidad del stock.");
                    } else {
                        JOptionPane.showMessageDialog(VistaPrestamos.this,
                                "No se realizó la devolución. El préstamo puede estar devuelto, no existir o haber un error de conexión.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(VistaPrestamos.this,
                            "No se pudo completar la devolución.");
                } finally {
                    cargarDatosPrestamosEnSegundoPlano();
                }
            }
        };
        worker.execute();
    }

    private void editarPrestamo() {
        if (!esBibliotecario() || !btnEditarPrestamo.isEnabled()) return;
        if (idPrestamoSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un préstamo pendiente.");
            return;
        }
        final LocalDate inicio;
        try {
            inicio = LocalDate.parse(txtFechaPrestamo.getText().trim());
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Usa fechas válidas con formato AAAA-MM-DD.");
            return;
        }
        final int id = idPrestamoSeleccionado;
        habilitarControlesPrestamos(false);
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return prestamos.actualizar(id, inicio);
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(VistaPrestamos.this, "Fechas actualizadas.");
                    } else {
                        JOptionPane.showMessageDialog(VistaPrestamos.this,
                                "No se actualizaron las fechas. Revisa las fechas, la conexión y que el préstamo siga pendiente.");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(VistaPrestamos.this, "No se pudo completar la edición.");
                } finally {
                    cargarDatosPrestamosEnSegundoPlano();
                }
            }
        };
        worker.execute();
    }

    private void limpiarPrestamo() {
        idPrestamoSeleccionado = -1;
        tblPrestamos.clearSelection();
        if (esBibliotecario()) cmbEstudiantes.setSelectedIndex(-1);
        else cmbEstudiantes.setSelectedItem(estudianteActual);
        cmbLibros.setSelectedIndex(-1);
        txtFechaPrestamo.setText(LocalDate.now().toString());
        actualizarVencimiento();
        chkDevuelto.setSelected(false);
        cmbEstudiantes.setEnabled(esBibliotecario());
        cmbLibros.setEnabled(true);
        actualizarBotonesSeleccion();
    }

    /**
     * Activa o desactiva controles y navegación durante las operaciones de préstamos.
     * @param habilitar true para restaurar los controles según sus permisos
     */
    private void habilitarControlesPrestamos(boolean habilitar) {
        btnAgregarPrestamo.setEnabled(habilitar);
        btnEditarPrestamo.setEnabled(habilitar && esBibliotecario());
        btnEliminarPrestamo.setEnabled(false);
        btnEliminarPrestamo.setVisible(false);
        btnDevolverPrestamo.setEnabled(habilitar);
        btnLimpiarPrestamo.setEnabled(habilitar);
        tblPrestamos.setEnabled(habilitar);
        txtFechaPrestamo.setEnabled(habilitar);
        txtFechaPrestamo.setEditable(esBibliotecario());
        txtFechaDevolucion.setEditable(false);
        txtFechaDevolucion.setEnabled(habilitar);
        cmbEstudiantes.setEnabled(habilitar && esBibliotecario() && idPrestamoSeleccionado <= 0);
        cmbLibros.setEnabled(habilitar && idPrestamoSeleccionado <= 0);
        // Impide entrar a editar stock desde otra pestaña durante la operación.
        ventana.habilitarNavegacion(habilitar);
        chkDevuelto.setEnabled(false);
        actualizarBotonesSeleccion();
    }

    /**
     * Consulta libros, estudiantes permitidos y préstamos fuera del hilo de eventos. Publica los resultados en done() y restaura los controles al terminar.
     */
    private void cargarDatosPrestamosEnSegundoPlano() {
        habilitarControlesPrestamos(false);
        SwingWorker<List<Prestamo>, Void> worker = new SwingWorker<List<Prestamo>, Void>() {
            private List<Libro> librosConsultados;
            private List<Estudiante> estudiantesConsultados;

            @Override
            protected List<Prestamo> doInBackground() {
                librosConsultados = controladorLibros.obtenerLibros();
                if (esBibliotecario()) {
                    estudiantesConsultados = controladorEstudiantes.obtenerEstudiantes();
                } else {
                    estudiantesConsultados = new ArrayList<>();
                    estudiantesConsultados.add(estudianteActual);
                }
                return prestamos.obtenerPrestamos();
            }

            @Override
            protected void done() {
                try {
                    List<Prestamo> prestamosConsultados = get();
                    // Actualiza la vista solo después de obtener todas las consultas de la recarga.
                    listaLibros = librosConsultados;
                    listaPrestamos = prestamosConsultados;

                    listaEstudiantes = estudiantesConsultados;
                    cargarCombosPrestamos();
                    mostrarTablaPrestamos();
                    limpiarPrestamo();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(VistaPrestamos.this,
                            "No se pudo refrescar la pantalla. Una operación ya confirmada sigue guardada. Reintenta con Actualizar listas.");
                } finally {
                    habilitarControlesPrestamos(true);
                }
            }
        };
        worker.execute();
    }

    private void mostrarTablaPrestamos() {
        tblPrestamos.clearSelection();
        modeloTablaPrestamos.setRowCount(0);
        for (Prestamo p : listaPrestamos) {
            String nombreEstudiante = "Sin estudiante";
            String tituloLibro = "Sin libro";
            for (Estudiante estudiante : listaEstudiantes) {
                if (p.getIdEstudiante() != null && estudiante.getId() == p.getIdEstudiante()) nombreEstudiante = estudiante.getNombre();
            }
            for (Libro libro : listaLibros) {
                if (p.getIdLibro() != null && libro.getId() == p.getIdLibro()) tituloLibro = libro.getTitulo();
            }
            modeloTablaPrestamos.addRow(new Object[]{p.getId(), nombreEstudiante, tituloLibro,
                    p.getFechaPrestamo(), p.getFechaVencimiento(),
                    Boolean.TRUE.equals(p.getDevuelto()) ? p.getFechaDevolucion() : null, p.getEstado()});
        }
    }

    /**
     * Actualiza la vista previa del vencimiento cuando cambia la fecha de inicio, sin consultar la base de datos.
     */
    private void actualizarVencimiento() {
        try {
            LocalDate inicio = LocalDate.parse(txtFechaPrestamo.getText().trim());
            txtFechaDevolucion.setText(prestamos.calcularVencimiento(inicio).toString());
        } catch (java.time.DateTimeException | IllegalArgumentException e) {
            txtFechaDevolucion.setText("");
        }
    }
    private boolean esBibliotecario() {
        return "bibliotecario".equals(usuarioActual.getRol());
    }
    /**
     * Obtiene el préstamo correspondiente a la fila seleccionada, convirtiendo el índice visual al índice del modelo.
     * @return préstamo seleccionado, o null si no hay una selección válida
     */
    private Prestamo obtenerPrestamoSeleccionado() {
        int fila = tblPrestamos.getSelectedRow();
        if (fila < 0) return null;
        int indice = tblPrestamos.convertRowIndexToModel(fila);
        if (indice >= listaPrestamos.size()) return null;
        return listaPrestamos.get(indice);
    }

    /**
     * Ajusta Devolver y Editar al estado del préstamo seleccionado y a la disponibilidad de la vista.
     */
    private void actualizarBotonesSeleccion() {
        Prestamo seleccionado = obtenerPrestamoSeleccionado();
        boolean pendiente = seleccionado != null
                && !Boolean.TRUE.equals(seleccionado.getDevuelto());
        boolean disponible = tblPrestamos.isEnabled();
        btnDevolverPrestamo.setEnabled(disponible && pendiente);
        btnEditarPrestamo.setEnabled(disponible && pendiente && esBibliotecario());
    }
}
