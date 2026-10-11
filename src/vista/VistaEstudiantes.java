package vista;

import controlador.ControladorEstudiantes;
import modelo.Estudiante;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenta el formulario, la selección y el CRUD de estudiantes mediante su controlador.
 */
public class VistaEstudiantes extends JPanel{
    private JLabel lblRutEstudiante;
    private JLabel lblNombreEstudiante;
    private JLabel lblCorreoEstudiante;
    private JLabel lblCursoEstudiante;
    private JTextField txtRutEstudiante;
    private JTextField txtNombreEstudiante;
    private JTextField txtCorreoEstudiante;
    private JTextField txtCursoEstudiante;
    private JButton btnAgregarEstudiante;
    private JButton btnEliminarEstudiante;
    private JButton btnLimpiarEstudiante;
    private JButton btnEditarEstudiante;
    private JTable tblEstudiantes;
    private JPanel jpEstudiantes;

    private final ControladorEstudiantes controladorEstudiantes;
    private final Usuario usuarioActual;
    private List<Estudiante> listaEstudiantes = new ArrayList<>();
    private DefaultTableModel modeloTablaEstudiantes;
    private int idEstudianteSeleccionado = -1;

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     * @param controladorEstudiantes controlador del CRUD de estudiantes
     * @param usuario usuario autenticado
     */
    public VistaEstudiantes(ControladorEstudiantes controladorEstudiantes, Usuario usuario) {
        this.controladorEstudiantes = controladorEstudiantes;
        this.usuarioActual = usuario;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jpEstudiantes, BorderLayout.CENTER);

        inicializarTablaEstudiantes();
        configurarEventos();
        actualizarTablaEstudiantes();
        limpiarEstudiante();
    }

    /**
     * Conecta los eventos de los controles con las acciones de la vista.
     */
    private void configurarEventos() {
        btnAgregarEstudiante.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarEstudiante();
            }
        });
        btnEditarEstudiante.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarEstudiante();
            }
        });
        btnEliminarEstudiante.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarEstudiante();
            }
        });
        btnLimpiarEstudiante.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarEstudiante();
            }
        });
    }


    private void inicializarTablaEstudiantes() {
        String[] columnas = {"ID", "RUT", "Nombre", "Curso", "Correo"};
        modeloTablaEstudiantes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblEstudiantes.setModel(modeloTablaEstudiantes);
        tblEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblEstudiantes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblEstudiantes.getSelectedRow() >= 0) {
                seleccionarEstudiante();
            }
        });
    }

    private void actualizarTablaEstudiantes() {
        try {
            listaEstudiantes = controladorEstudiantes.obtenerEstudiantes();
            tblEstudiantes.clearSelection();
            idEstudianteSeleccionado = -1;
            modeloTablaEstudiantes.setRowCount(0);
            for (Estudiante registro : listaEstudiantes) {
                Object[] fila = {registro.getId(), registro.getRut(), registro.getNombre(), registro.getCurso(), registro.getCorreo()};
                modeloTablaEstudiantes.addRow(fila);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al cargar estudiantes", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void agregarEstudiante() {
        if (!esBibliotecario()) {
            return;
        }
        try {
            String nombre = txtNombreEstudiante.getText().trim();
            String rut = txtRutEstudiante.getText().trim();
            String curso = txtCursoEstudiante.getText().trim();
            String correo = txtCorreoEstudiante.getText().trim();
            boolean resultado = controladorEstudiantes.registrarEstudiante(nombre, rut, curso, correo);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Estudiante agregado correctamente.");
                actualizarTablaEstudiantes();
                limpiarEstudiante();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarEstudiante() {
        if (!esBibliotecario()) {
            return;
        }
        if (idEstudianteSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante de la tabla.");
            return;
        }
        try {
            String nombre = txtNombreEstudiante.getText().trim();
            String rut = txtRutEstudiante.getText().trim();
            String curso = txtCursoEstudiante.getText().trim();
            String correo = txtCorreoEstudiante.getText().trim();
            boolean resultado = controladorEstudiantes.actualizar(idEstudianteSeleccionado, nombre, rut, curso, correo);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Estudiante actualizado correctamente.");
                actualizarTablaEstudiantes();
                limpiarEstudiante();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEstudiante() {
        if (!esBibliotecario()) {
            return;
        }
        if (idEstudianteSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un estudiante de la tabla.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar el estudiante seleccionado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean resultado = controladorEstudiantes.eliminar(idEstudianteSeleccionado);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Estudiante eliminado correctamente.");
                actualizarTablaEstudiantes();
                limpiarEstudiante();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void seleccionarEstudiante() {
        Estudiante e = listaEstudiantes.get(tblEstudiantes.getSelectedRow());
        idEstudianteSeleccionado = e.getId();
        txtRutEstudiante.setText(e.getRut()); txtNombreEstudiante.setText(e.getNombre()); txtCursoEstudiante.setText(e.getCurso()); txtCorreoEstudiante.setText(e.getCorreo());
    }

    private void limpiarEstudiante() {
        idEstudianteSeleccionado = -1;
        tblEstudiantes.clearSelection();
        txtRutEstudiante.setText("");
        txtNombreEstudiante.setText("");
        txtCursoEstudiante.setText("");
        txtCorreoEstudiante.setText("");
    }

    /**
     * Vuelve a cargar los datos de la vista y limpia su selección. Debe llamarse desde el hilo de eventos de Swing.
     */
    public void refrescar() {
        actualizarTablaEstudiantes();
        limpiarEstudiante();
    }
    private boolean esBibliotecario() {
        return "bibliotecario".equals(usuarioActual.getRol());
    }
}
