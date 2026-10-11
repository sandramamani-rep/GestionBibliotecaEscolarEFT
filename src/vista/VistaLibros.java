package vista;

import controlador.ControladorCategorias;
import controlador.ControladorEstudiantes;
import controlador.ControladorLibros;
import modelo.Categoria;
import modelo.Libro;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Presenta el catálogo y el CRUD de libros. Las modificaciones se reservan al bibliotecario.
 */
public class VistaLibros extends JPanel {
    private JPanel jpLibros;
    private JLabel lblTituloLibro;
    private JTextField txtTituloLibro;
    private JLabel lblAutorLibro;
    private JLabel lblIsbnLibro;
    private JLabel lblEditorialLibro;
    private JLabel lblStock;
    private JLabel lblCategoria;
    private JTextField txtAutorLibro;
    private JTextField txtIsbnLibro;
    private JTextField txtEditorial;
    private JSpinner spStock;
    private JComboBox cmbCategorias;
    private JButton btnAgregarLibro;
    private JButton btnEditarLibro;
    private JButton btnEliminarLibro;
    private JButton btnLimpiarLibro;
    private JTable tblLibros;

    private final ControladorLibros controladorLibros;
    private final ControladorCategorias controladorCategorias;
    private final Usuario usuarioActual;
    private int idLibroSeleccionado = -1;
    private DefaultTableModel modeloTablaLibros;
    private List<Libro> listaLibros = new ArrayList<>();
    private List<Categoria> listaCategorias = new ArrayList<>();

    /**
     * Crea la vista y conecta sus componentes con los controladores.
     * @param controladorLibros controlador del catálogo de libros
     * @param controladorCategorias controlador del CRUD de categorías
     * @param usuario usuario autenticado
     */
    public VistaLibros(ControladorLibros controladorLibros, ControladorCategorias controladorCategorias, Usuario usuario) {
        this.controladorLibros = controladorLibros;
        this.controladorCategorias = controladorCategorias;
        this.usuarioActual = usuario;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jpLibros, BorderLayout.CENTER);
        cargarCategorias();
        inicializarTablaLibros();
        configurarEventos();
        actualizarTablaLibros();
        limpiarLibro();
        aplicarPermisos();
    }

    /**
     * Conecta los eventos de los controles con las acciones de la vista.
     */
    private void configurarEventos() {
        btnAgregarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarLibro();
            }
        });
        btnEditarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarLibro();
            }
        });
        btnEliminarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarLibro();
            }
        });
        btnLimpiarLibro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarLibro();
            }
        });
    }


    private void inicializarTablaLibros() {
        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Editorial", "Stock", "Categoría"};
        modeloTablaLibros = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
        tblLibros.setModel(modeloTablaLibros);
        tblLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblLibros.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblLibros.getSelectedRow() >= 0) {
                seleccionarLibro();
            }
        });
    }

    private void actualizarTablaLibros() {
        try {
            listaLibros = controladorLibros.obtenerLibros();
            tblLibros.clearSelection();
            idLibroSeleccionado = -1;
            modeloTablaLibros.setRowCount(0);
            for (Libro registro : listaLibros) {
                Object[] fila = {registro.getId(), registro.getTitulo(), registro.getAutor(), registro.getIsbn(), registro.getEditorial(), registro.getStock(), nombreCategoria(listaCategorias, registro.getIdCategoria())};
                modeloTablaLibros.addRow(fila);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al cargar libros", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String nombreCategoria(List<Categoria> lista, Integer id) {
        for (Categoria categoria : lista) {
            if (id != null && categoria.getId() == id) return categoria.getNombre();
        }
        return "Sin categoría";
    }

    private void agregarLibro() {
        if (!esBibliotecario()) {
            return;
        }
        try {
            String titulo = txtTituloLibro.getText().trim();
            String autor = txtAutorLibro.getText().trim();
            String isbn = txtIsbnLibro.getText().trim();
            String editorial = txtEditorial.getText().trim();
            spStock.commitEdit();
            int stock = ((Number) spStock.getValue()).intValue();
            Categoria categoria = (Categoria) cmbCategorias.getSelectedItem();
            if (categoria == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría.");
                return;
            }
            int idCategoria = categoria.getId();
            boolean resultado = controladorLibros.registrarLibro(titulo, autor, isbn, editorial, stock, idCategoria);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Libro agregado correctamente.");
                actualizarTablaLibros();
                limpiarLibro();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "Ingresa un stock entero válido.");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarLibro() {
        if (!esBibliotecario()) {
            return;
        }
        if (idLibroSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro de la tabla.");
            return;
        }
        try {
            String titulo = txtTituloLibro.getText().trim();
            String autor = txtAutorLibro.getText().trim();
            String isbn = txtIsbnLibro.getText().trim();
            String editorial = txtEditorial.getText().trim();
            spStock.commitEdit();
            int stock = ((Number) spStock.getValue()).intValue();
            Categoria categoria = (Categoria) cmbCategorias.getSelectedItem();
            if (categoria == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría.");
                return;
            }
            int idCategoria = categoria.getId();
            boolean resultado = controladorLibros.actualizar(idLibroSeleccionado, titulo, autor, isbn, editorial, stock, idCategoria);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Libro actualizado correctamente.");
                actualizarTablaLibros();
                limpiarLibro();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (java.text.ParseException e) {
            JOptionPane.showMessageDialog(this, "Ingresa un stock entero válido.");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarLibro() {
        if (!esBibliotecario()) {
            return;
        }
        if (idLibroSeleccionado <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un libro de la tabla.");
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Deseas eliminar el libro seleccionado?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            boolean resultado = controladorLibros.eliminar(idLibroSeleccionado);
            if (resultado) {
                JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
                actualizarTablaLibros();
                limpiarLibro();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo completar la operación. Revisa los datos y el registro seleccionado.");
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void seleccionarLibro() {
        Libro l = listaLibros.get(tblLibros.getSelectedRow());
        idLibroSeleccionado = l.getId();
        txtTituloLibro.setText(l.getTitulo()); txtAutorLibro.setText(l.getAutor()); txtIsbnLibro.setText(l.getIsbn()); txtEditorial.setText(l.getEditorial());
        spStock.setValue(l.getStock() == null ? 0 : Math.max(0, l.getStock())); seleccionarCategoriaEnCombo(l.getIdCategoria());
    }

    private void seleccionarCategoriaEnCombo(Integer idCategoria) {
        cmbCategorias.setSelectedIndex(-1);
        if (idCategoria == null) {
            return;
        }
        for (int i = 0; i < cmbCategorias.getItemCount(); i++) {
            Categoria categoria = (Categoria) cmbCategorias.getItemAt(i);
            if (categoria.getId() == idCategoria) {
                cmbCategorias.setSelectedIndex(i);
                return;
            }
        }
    }
    private void limpiarLibro() {
        idLibroSeleccionado = -1;
        tblLibros.clearSelection(); txtTituloLibro.setText(""); txtAutorLibro.setText(""); txtIsbnLibro.setText(""); txtEditorial.setText(""); spStock.setValue(0); cmbCategorias.setSelectedIndex(-1);
    }

    private void cargarCategorias() {
        try {
            listaCategorias = controladorCategorias.obtenerCategorias();
            cmbCategorias.removeAllItems();
            for (Categoria categoria : listaCategorias) {
                cmbCategorias.addItem(categoria);
            }
            cmbCategorias.setSelectedIndex(-1);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error al cargar categorías", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Vuelve a cargar los datos de la vista y limpia su selección. Debe llamarse desde el hilo de eventos de Swing.
     */
    public void refrescar() {
        cargarCategorias();
        actualizarTablaLibros();
        limpiarLibro();
        aplicarPermisos();
    }

    /**
     * Aplica los permisos del usuario conectado a los controles o pestañas de esta vista.
     */
    private void aplicarPermisos() {
        boolean puedeEditar = esBibliotecario();
        btnAgregarLibro.setEnabled(puedeEditar);
        btnEditarLibro.setEnabled(puedeEditar);
        btnEliminarLibro.setEnabled(puedeEditar);
        btnLimpiarLibro.setEnabled(puedeEditar);
        txtTituloLibro.setEditable(puedeEditar);
        txtAutorLibro.setEditable(puedeEditar);
        txtIsbnLibro.setEditable(puedeEditar);
        txtEditorial.setEditable(puedeEditar);
        spStock.setEnabled(puedeEditar);
        cmbCategorias.setEnabled(puedeEditar);
    }
    private boolean esBibliotecario() {
        return "bibliotecario".equals(usuarioActual.getRol());
    }
}
