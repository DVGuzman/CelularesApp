import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.regex.Pattern;

class CelularManager {
    private String[] marcas;
    private String[] modelos;
    private double[][] precios;
    private final int ANIOS = 5;
    private final int[] years = {2021, 2022, 2023, 2024, 2025};

    public CelularManager(int n) {
        marcas = new String[n];
        modelos = new String[n];
        precios = new double[n][ANIOS];
    }

    // ===== Getters y Setters =====
    public String getMarca(int i) {
        return marcas[i];
    }

    public void setMarca(int i, String marca) {
        marcas[i] = marca;
    }

    public String getModelo(int i) {
        return modelos[i];
    }

    public void setModelo(int i, String modelo) {
        modelos[i] = modelo;
    }

    public double getPrecio(int i, int j) {
        return precios[i][j];
    }

    public void setPrecio(int i, int j, double precio) {
        precios[i][j] = precio;
    }

    public int getCantidad() {
        return marcas.length;
    }

    public int[] getYears() {
        return years;
    }

    // ===== Consultas =====

    // 1. Info de un celular específico
    public String getInfoCelular(int index) {
        if (index < 0 || index >= marcas.length) {
            return "Celular no encontrado";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(marcas[index]).append(" ").append(modelos[index]).append("\n\n");

        for (int j = 0; j < ANIOS; j++) {
            sb.append(years[j])
              .append(": $")
              .append(precios[index][j])
              .append("\n");
        }

        return sb.toString();
    }

    // 2. Más barato por año
    public String getMasBaratosPorAnio() {
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < ANIOS; j++) {
            double min = precios[0][j];
            String res = marcas[0] + " " + modelos[0];

            for (int i = 1; i < marcas.length; i++) {
                if (precios[i][j] < min) {
                    min = precios[i][j];
                    res = marcas[i] + " " + modelos[i];
                }
            }

            sb.append(years[j])
              .append(": ")
              .append(res)
              .append(" ($")
              .append(min)
              .append(")\n");
        }

        return sb.toString();
    }

    // 3. Más caro por año
    public String getMasCarosPorAnio() {
        StringBuilder sb = new StringBuilder();

        for (int j = 0; j < ANIOS; j++) {
            double max = precios[0][j];
            String res = marcas[0] + " " + modelos[0];

            for (int i = 1; i < marcas.length; i++) {
                if (precios[i][j] > max) {
                    max = precios[i][j];
                    res = marcas[i] + " " + modelos[i];
                }
            }

            sb.append(years[j])
              .append(": ")
              .append(res)
              .append(" ($")
              .append(max)
              .append(")\n");
        }

        return sb.toString();
    }

    // 4. Promedio real por año
    public String getPromedioAnio(int col) {
        double suma = 0;
        int cont = 0;

        for (int i = 0; i < marcas.length; i++) {
            suma += precios[i][col];
            cont++;
        }

        if (cont > 0) {
            double prom = suma / cont;
            return "$" + prom;
        }

        return "No hay datos";
    }

    // Promedio por rango
    public String getPromedioRango(int col) {
        double suma = 0;
        int cont = 0;

        for (int i = 0; i < marcas.length; i++) {
            double p = precios[i][col];

            if (p >= 1500000 && p <= 3000000) {
                suma += p;
                cont++;
            }
        }

        if (cont > 0) {
            return "$" + (suma / cont);
        } else {
            return "No hay datos en el rango";
        }
    }
}

public class CelularesApp extends JFrame {

    private CelularManager manager;
    private JTextField txtCantidad;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> comboYear;

    // Validación
    private final Pattern TEXTO_VALIDO = Pattern.compile("^[a-zA-Z0-9 ]+$");

    public CelularesApp() {
        setTitle("Gestión de Celulares");
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // ===== Panel superior =====
        JPanel top = new JPanel();
        top.add(new JLabel("Cantidad de celulares:"));

        txtCantidad = new JTextField(5);
        top.add(txtCantidad);

        JButton btnCrear = new JButton("Crear Tabla");
        top.add(btnCrear);

        add(top, BorderLayout.NORTH);

        // ===== Tabla =====
        model = new DefaultTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // ===== Panel inferior =====
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

        // ===== Eventos =====
        btnCrear.addActionListener(e -> crearTabla());
        btnGuardar.addActionListener(e -> cargarDatos());
        btnBuscar.addActionListener(e -> buscarCelular());
        btnBaratos.addActionListener(e -> mostrarBaratos());
        btnCaros.addActionListener(e -> mostrarCaros());
        btnProm.addActionListener(e -> mostrarPromedio());
        btnPromRango.addActionListener(e -> mostrarPromedioRango());
    }

    // ===== Crear tabla =====
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

    // ===== Guardar datos =====
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

                    double precio = Double.parseDouble(dato);

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
            mostrarError("Los precios deben ser numéricos");
        } catch (Exception e) {
            mostrarError(e.getMessage());
        }
    }

    // ===== Buscar celular =====
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

    // ===== Mostrar baratos =====
    private void mostrarBaratos() {
        if (manager == null) return;

        String res = manager.getMasBaratosPorAnio();

        JOptionPane.showMessageDialog(this, res,
                "Más Baratos", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== Mostrar caros =====
    private void mostrarCaros() {
        if (manager == null) return;

        String res = manager.getMasCarosPorAnio();

        JOptionPane.showMessageDialog(this, res,
                "Más Caros", JOptionPane.INFORMATION_MESSAGE);
    }

    // ===== Promedio normal =====
    private void mostrarPromedio() {
        if (manager == null) return;

        int col = comboYear.getSelectedIndex();

        String res = manager.getPromedioAnio(col);

        JOptionPane.showMessageDialog(this,
                "Promedio " + comboYear.getSelectedItem() + ":\n" + res);
    }

    // ===== Promedio por rango =====
    private void mostrarPromedioRango() {
        if (manager == null) return;

        int col = comboYear.getSelectedIndex();

        String res = manager.getPromedioRango(col);

        JOptionPane.showMessageDialog(this,
                "Promedio (1.5M - 3M):\n" + res);
    }

    // ===== Validar texto =====
    private void validarTexto(String texto, String campo, int fila) throws Exception {
        if (texto.isEmpty())
            throw new Exception(campo + " vacío en fila " + (fila + 1));

        if (!TEXTO_VALIDO.matcher(texto).matches())
            throw new Exception(campo + " inválido en fila " + (fila + 1));
    }

    // ===== Errores =====
    private void mostrarError(String msg) {
        JOptionPane.showMessageDialog(this,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // ===== Main =====
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CelularesApp().setVisible(true);
        });
    }
}
