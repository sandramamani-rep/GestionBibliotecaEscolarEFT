package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import controlador.ControladorUsuarios;
import controlador.ControladorEstudiantes;
import modelo.*;

/**
 * Presenta el CRUD de cuentas para el bibliotecario. Impide eliminar la cuenta conectada o quitarle su rol durante la sesión.
 */
public class VistaUsuarios extends JPanel {
    private JLabel lblRut;
    private JLabel lblNombre;
    private JLabel lblCorreo;
    private JLabel lblContrasenia;
    private JLabel lblRol;
    private JTextField txtRutUsuario;
    private JTextField txtNombreUsuario;
    private JTextField txtCorreoUsuario;
    private JPasswordField txtContrasenia;
    private JComboBox<String> cmbRol;
    private JButton btnAgregar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JTable tblUsuarios;
    private JPanel jpUsuarios;
    private JComboBox<Estudiante> cmbEstudiantesSinUsuario;
    private final ControladorEstudiantes controladorEstudiantes = new ControladorEstudiantes();
    private final ControladorUsuarios controladorUsuarios;
    private final Usuario usuarioActual;
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private DefaultTableModel modeloTablaUsuarios;
    private int idUsuarioSeleccionado = -1;

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     * @param controladorUsuarios controlador del CRUD de usuarios
     * @param usuario usuario autenticado
     */
    public VistaUsuarios(ControladorUsuarios controladorUsuarios, Usuario usuario) {
        this.controladorUsuarios = controladorUsuarios;
        this.usuarioActual = usuario;
        if (!esBibliotecario()) throw new IllegalArgumentException("Acceso exclusivo del bibliotecario.");
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jpUsuarios, BorderLayout.CENTER);
        cmbRol.removeAllItems();
        for (Rol rol : Rol.values()) cmbRol.addItem(rol.name());
        txtContrasenia.setToolTipText("Al editar, deja vacío para conservar la contraseña actual");
        inicializarTablaUsuarios();
        configurarEventos();
        refrescar();
    }

    /**
     * Vuelve a cargar los datos de la vista y limpia su selección. Debe llamarse desde el hilo de eventos de Swing.
     */
    public void refrescar() {
        actualizarTablaUsuarios();
        limpiarUsuario();
    }

    /**
     * Conecta los eventos de los controles con las acciones de la vista.
     */
    private void configurarEventos() {
        cmbEstudiantesSinUsuario.addActionListener(e -> autocompletarEstudiante());
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { agregarUsuario(); }
        });
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { editarUsuario(); }
        });
        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { eliminarUsuario(); }
        });
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { limpiarUsuario(); }
        });
    }

    private void agregarUsuario() {
        if (!esBibliotecario()) {
            return;
        }
        try {
            String nombre = txtNombreUsuario.getText().trim();
            String rut = txtRutUsuario.getText().trim();
            String correo = txtCorreoUsuario.getText().trim();
            String contrasenia = new String(txtContrasenia.getPassword());
            if (cmbRol.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un rol.");
                return;
            }
            String rol = cmbRol.getSelectedItem().toString();
            boolean resultado = controladorUsuarios.registrarUsuario(nombre, rut, correo, contrasenia, rol);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Usuario agregado correctamente.");
                actualizarTablaUsuarios();
                limpiarUsuario();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarUsuario() {
        if (!esBibliotecario()) {
            return;
        }
        if (idUsuarioSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla.");
            return;
        }
        try {
            String nombre = txtNombreUsuario.getText().trim();
            String rut = txtRutUsuario.getText().trim();
            String correo = txtCorreoUsuario.getText().trim();
            String contrasenia = new String(txtContrasenia.getPassword());
            if (cmbRol.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un rol.");
                return;
            }
            String rol = cmbRol.getSelectedItem().toString();
            if (idUsuarioSeleccionado == usuarioActual.getId() && !rol.equals("bibliotecario")) {
                JOptionPane.showMessageDialog(this, "No puedes quitarte el rol bibliotecario durante la sesión.");
                return;
            }
            boolean resultado = controladorUsuarios.actualizar(idUsuarioSeleccionado, nombre, rut, correo, contrasenia, rol);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Usuario actualizado correctamente.");
                actualizarTablaUsuarios();
                limpiarUsuario();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarUsuario() {
        if (!esBibliotecario()) {
            return;
        }
        if (idUsuarioSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario de la tabla.");
            return;
        }
        if (idUsuarioSeleccionado == usuarioActual.getId()) {
            JOptionPane.showMessageDialog(this, "No puedes eliminar tu propia cuenta durante la sesión.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar el usuario seleccionado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean resultado = controladorUsuarios.eliminar(idUsuarioSeleccionado);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente.");
                actualizarTablaUsuarios();
                limpiarUsuario();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void inicializarTablaUsuarios() {
        String[] columnas = {"ID", "RUT", "Nombre", "Correo", "Rol"};
        modeloTablaUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblUsuarios.setModel(modeloTablaUsuarios);
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblUsuarios.getSelectedRow() >= 0) {
                seleccionarUsuario();
            }
        });
    }

    private void actualizarTablaUsuarios() {
        try {
            listaUsuarios = controladorUsuarios.obtenerUsuarios();
            tblUsuarios.clearSelection();
            idUsuarioSeleccionado = -1;
            modeloTablaUsuarios.setRowCount(0);
            for (Usuario registro : listaUsuarios) {
                Object[] fila = {registro.getId(), registro.getRut(), registro.getNombre(), registro.getCorreo(), registro.getRol()};
                modeloTablaUsuarios.addRow(fila);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al cargar usuarios", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarUsuario() {
        Usuario u = listaUsuarios.get(tblUsuarios.convertRowIndexToModel(tblUsuarios.getSelectedRow()));
        idUsuarioSeleccionado = u.getId();
        cmbEstudiantesSinUsuario.setSelectedIndex(-1);
        cmbEstudiantesSinUsuario.setEnabled(false);
        habilitarDatosPersonales(true);
        txtRutUsuario.setText(u.getRut()); txtNombreUsuario.setText(u.getNombre()); txtCorreoUsuario.setText(u.getCorreo());
        txtContrasenia.setText(""); cmbRol.setSelectedItem(u.getRol());
    }

    private void limpiarUsuario() {
        idUsuarioSeleccionado = -1;
        tblUsuarios.clearSelection(); txtRutUsuario.setText(""); txtNombreUsuario.setText(""); txtCorreoUsuario.setText(""); txtContrasenia.setText(""); cmbRol.setSelectedItem("estudiante");
        habilitarDatosPersonales(true);
        cargarEstudiantesSinUsuario();
    }

    private boolean esBibliotecario() {
        return usuarioActual != null && "bibliotecario".equals(usuarioActual.getRol());
    }

    /** Recarga las opciones sin seleccionar automáticamente a un estudiante. */
    private void cargarEstudiantesSinUsuario() {
        DefaultComboBoxModel<Estudiante> modelo = new DefaultComboBoxModel<>();
        try {
            for (Estudiante estudiante : controladorEstudiantes.obtenerEstudiantesSinUsuario()) {
                modelo.addElement(estudiante);
            }
            modelo.setSelectedItem(null);
            cmbEstudiantesSinUsuario.setModel(modelo);
            cmbEstudiantesSinUsuario.setEnabled(modelo.getSize() > 0);
            cmbEstudiantesSinUsuario.setToolTipText(modelo.getSize() > 0
                    ? "Opcional: selecciona un estudiante o ingresa los datos manualmente."
                    : "No hay estudiantes sin usuario. Puedes ingresar los datos manualmente.");
        } catch (IllegalStateException e) {
            cmbEstudiantesSinUsuario.setModel(modelo);
            cmbEstudiantesSinUsuario.setEnabled(false);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Completa una cuenta nueva a partir del estudiante elegido; no modifica su ficha. */
    private void autocompletarEstudiante() {
        if (idUsuarioSeleccionado > 0) return;
        Estudiante estudiante = (Estudiante) cmbEstudiantesSinUsuario.getSelectedItem();
        if (estudiante == null) return;
        tblUsuarios.clearSelection();
        txtRutUsuario.setText(estudiante.getRut());
        txtNombreUsuario.setText(estudiante.getNombre());
        txtCorreoUsuario.setText(estudiante.getCorreo());
        txtContrasenia.setText("");
        cmbRol.setSelectedItem("estudiante");
        habilitarDatosPersonales(false);
        txtContrasenia.requestFocusInWindow();
    }

    /**
     * Permite ingreso manual o conserva los datos de la ficha seleccionada.
     * @param habilitar true para permitir editar RUT, nombre y correo
     */
    private void habilitarDatosPersonales(boolean habilitar) {
        txtRutUsuario.setEditable(habilitar);
        txtNombreUsuario.setEditable(habilitar);
        txtCorreoUsuario.setEditable(habilitar);
    }

}
