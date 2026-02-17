import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.regex.Pattern;

public class CelularesApp extends JFrame {

    private CelularManager manager;
    private JTextField txtCantidad;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> comboYear;

    private final Pattern TEXTO_VALIDO = Pattern.compile("^[a-zA-Z0-9 ]+$");

    public CelularesApp() {
        setTitle("Gestión de Celulares");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.add(new JLabel("Cantidad de celulares:"));

        txtCantidad = new JTextField(5);
        top.add(txtCantidad);

        JButton btnCrear = new JButton("Crear Tabla");
        top.add(btnCrear);

        add(top, BorderLayout.NORTH);

        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel();

        JButton btnGuardar = new JButton("Guardar");
        JButton btnBuscar = new JButton("Buscar Celular");
        JButton btnBaratos = new JButton("Baratos por Año");
        JButton btnCaros = new JButton("Caros por Año");
        JButton btnProm = new JButton("Promedio Año");
        JButton btnPromRango = new JButton("Promedio Rango");

        comboYear = new JComboBox<>(new String[]{
                "2021", "2022", "2023", "2024", "2025"
        });

        bottom.add(btnGuardar);
        bottom.add(btnBuscar);
        bottom.add(btnBaratos);
        bottom.add(btnCaros);
        bottom.add(comboYear);
        bottom.add(btnProm);
        bottom.add(btnPromRango);

        add(bottom, BorderLayout.SOUTH);

        btnCrear.addActionListener(e -> crearTabla());
        btnGuardar.addActionListener(e -> cargarDatos());
        btnBuscar.addActionListener(e -> buscarCelular());
        btnBaratos.addActionListener(e -> mostrarBaratos());
        btnCaros.addActionListener(e -> mostrarCaros());
        btnProm.addActionListener(e -> mostrarPromedio());
        btnPromRango.addActionListener(e -> mostrarPromedioRango());
    }

    private void crearTabla() {
        try {
            int n = Integer.parseInt(txtCantidad.getText());

            if (n <= 0) throw new NumberFormatException();

            manager = new CelularManager(n);

            model.setRowCount(0);
            model.setColumnCount(0);

            model.addColumn("Marca");
            model.addColumn("Modelo");
            model.addColumn("2021");
            model.addColumn("2022");
            model.addColumn("2023");
            model.addColumn("2024");
            model.addColumn("2025");

            for (int i = 0; i < n; i++) {
                model.addRow(new Object[]{"", "", "", "", "", "", ""});
            }

        } catch (NumberFormatException ex) {
            mostrarError("Ingrese un número válido mayor que 0");
        }
    }

    private void cargarDatos() {
        if (manager == null) {
            mostrarError("Primero cree la tabla");
            return;
        }

        try {
            for (int i = 0; i < model.getRowCount(); i++) {

                String marca = model.getValueAt(i, 0).toString().trim();
                validarTexto(marca, "Marca", i);
                manager.setMarca(i, marca);

                String modelo = model.getValueAt(i, 1).toString().trim();
                validarTexto(modelo, "Modelo", i);
                manager.setModelo(i, modelo);

                for (int j = 2; j <= 6; j++) {
                    String dato = model.getValueAt(i, j).toString().trim();

                    if (dato.isEmpty())
                        throw new Exception("Campo vacío en fila " + (i + 1));

                    // Cambia aquí: acepta valores con puntos como separador de miles
                    int precio = CelularManager.parsePrecio(dato);

                    if (precio <= 0)
                        throw new Exception("Precio inválido en fila " + (i + 1));

                    manager.setPrecio(i, j - 2, precio);
                }
            }

            JOptionPane.showMessageDialog(this,
                    "Datos guardados correctamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            mostrarError("Los precios deben ser numéricos (ej: 1.550.000)");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    private void buscarCelular() {
        if (manager == null) return;

        String input = JOptionPane.showInputDialog(
                this,
                "Ingrese el número del celular (1 - " + manager.getCantidad() + ")"
        );

        try {
            int pos = Integer.parseInt(input) - 1;

            String info = manager.getInfoCelular(pos);

            JOptionPane.showMessageDialog(this, info,
                    "Información", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            mostrarError("Número inválido");
        }
    }

    private void mostrarBaratos() {
        if (manager == null) return;

        String res = manager.getMasBaratosPorYear_Year();

        JOptionPane.showMessageDialog(this, res,
                "Más Baratos", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarCaros() {
        if (manager == null) return;

        String res = manager.getMasCarosPorYear_Year();

        JOptionPane.showMessageDialog(this, res,
                "Más Caros", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarPromedio() {
        if (manager == null) return;

        int col = comboYear.getSelectedIndex();

        String res = manager.getPromedioYear_Year(col);

        JOptionPane.showMessageDialog(this,
                "Promedio " + comboYear.getSelectedItem() + ":\n" + res);
    }

    private void mostrarPromedioRango() {
        if (manager == null) return;

        int col = comboYear.getSelectedIndex();

        String res = manager.getPromedioRango(col);

        JOptionPane.showMessageDialog(this,
                "Promedio (1.5M - 3M):\n" + res);
    }

    private void validarTexto(String texto, String campo, int fila) throws Exception {
        if (texto.isEmpty())
            throw new Exception(campo + " vacío en fila " + (fila + 1));

        if (!TEXTO_VALIDO.matcher(texto).matches())
            throw new Exception(campo + " inválido en fila " + (fila + 1));
    }

    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CelularesApp().setVisible(true);
        });
    }
}
