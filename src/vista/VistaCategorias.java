package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import controlador.ControladorCategorias;
import modelo.*;

/**
 * Presenta el CRUD de categorías para el bibliotecario.
 */
public class VistaCategorias extends JPanel {
    private JLabel lblNombreCategoria;
    private JTextField txtNombreCategoria;
    private JButton btnAgregarCategoria;
    private JButton btnLimpiarCategoria;
    private JButton btnEditarCategoria;
    private JButton btnEliminarCategoria;
    private JTable tblCategorias;
    private JPanel jpCategorias;
    private final ControladorCategorias categorias;
    private final Usuario usuarioActual;
    private List<Categoria> listaCategorias = new ArrayList<>();
    private DefaultTableModel modeloTablaCategorias;
    private int idCategoriaSeleccionada = -1;

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     * @param controladorCategorias controlador del CRUD de categorías
     * @param usuario usuario autenticado
     */
    public VistaCategorias(ControladorCategorias controladorCategorias, Usuario usuario) {
        this.categorias = controladorCategorias;
        this.usuarioActual = usuario;
        if (!esBibliotecario()) throw new IllegalArgumentException("Acceso exclusivo del bibliotecario.");
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jpCategorias, BorderLayout.CENTER);
        configurarCategorias();
        refrescar();
    }

    /**
     * Vuelve a cargar los datos de la vista y limpia su selección. Debe llamarse desde el hilo de eventos de Swing.
     */
    public void refrescar() {
        actualizarTablaCategorias();
    }

    private void configurarCategorias() {


        modeloTablaCategorias = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tblCategorias.setModel(modeloTablaCategorias);
        tblCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCategorias.getSelectionModel().addListSelectionListener(e -> {
            int fila = tblCategorias.getSelectedRow();
            if (!e.getValueIsAdjusting() && fila >= 0) {
                fila = tblCategorias.convertRowIndexToModel(fila);
                idCategoriaSeleccionada = (int) modeloTablaCategorias.getValueAt(fila, 0);
                txtNombreCategoria.setText((String) modeloTablaCategorias.getValueAt(fila, 1));
            }
        });
        btnAgregarCategoria.addActionListener(e -> agregarCategoria());
        btnEditarCategoria.addActionListener(e -> editarCategoria());
        btnEliminarCategoria.addActionListener(e -> eliminarCategoria());
        btnLimpiarCategoria.addActionListener(e -> limpiarCategoria());
    }

    private void actualizarTablaCategorias() {
        try {
            listaCategorias = categorias.obtenerCategorias();
            limpiarCategoria();
            modeloTablaCategorias.setRowCount(0);
            for (Categoria c : listaCategorias) {
                modeloTablaCategorias.addRow(new Object[]{c.getId(), c.getNombre()});
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarCategoria() {
        if (!esBibliotecario()) return;
        boolean resultado = categorias.registrarCategoria(txtNombreCategoria.getText());
        if (resultado) {
            JOptionPane.showMessageDialog(this, "Categoría agregada.");
            actualizarTablaCategorias();
            limpiarCategoria();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo agregar. Revisa el nombre y la conexión.");
        }
    }

    private void editarCategoria() {
        if (!esBibliotecario()) return;
        if (idCategoriaSeleccionada <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría.");
            return;
        }
        if (categorias.actualizar(idCategoriaSeleccionada, txtNombreCategoria.getText())) {
            JOptionPane.showMessageDialog(this, "Categoría actualizada.");
            actualizarTablaCategorias();

            limpiarCategoria();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar. Revisa el nombre y la conexión.");
        }
    }

    private void eliminarCategoria() {
        if (!esBibliotecario()) return;
        if (idCategoriaSeleccionada <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoría.");
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Eliminar la categoría seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;
        if (categorias.eliminar(idCategoriaSeleccionada)) {
            JOptionPane.showMessageDialog(this, "Categoría eliminada.");
            actualizarTablaCategorias();
            limpiarCategoria();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar. Comprueba si hay libros asociados y la conexión.");
        }
    }

    private void limpiarCategoria() {
        idCategoriaSeleccionada = -1;
        txtNombreCategoria.setText("");
        tblCategorias.clearSelection();
    }

    private boolean esBibliotecario() {
        return usuarioActual != null && "bibliotecario".equals(usuarioActual.getRol());
    }

}
