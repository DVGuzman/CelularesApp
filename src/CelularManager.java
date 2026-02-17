import java.text.DecimalFormat;

public class CelularManager {
    private String[] marcas;
    private String[] modelos;
    private int[][] precios; 
    private final int years_years = 5;
    private final int[] years = {2021, 2022, 2023, 2024, 2025};
    private final DecimalFormat formatoPesos = new DecimalFormat("#,###");

    public CelularManager(int n) {
        marcas = new String[n];
        modelos = new String[n];
        precios = new int[n][years_years];
    }

    // ===== Getters y Setters =====
    public String getMarca(int i) { return marcas[i]; }
    public void setMarca(int i, String marca) { marcas[i] = marca; }
    public String getModelo(int i) { return modelos[i]; }
    public void setModelo(int i, String modelo) { modelos[i] = modelo; }
    public int getPrecio(int i, int j) { return precios[i][j]; }
    public void setPrecio(int i, int j, int precio) { precios[i][j] = precio; }
    public int getCantidad() { return marcas.length; }
    public int[] getYears() { return years; }

    // ===== Utilidad para formatear y parsear precios =====
    public static int parsePrecio(String valor) throws NumberFormatException {
        // Elimina puntos y espacios, luego convierte a int
        return Integer.parseInt(valor.replace(".", "").replace(" ", ""));
    }

    public String formatPrecio(int valor) {
        return formatoPesos.format(valor).replace(",", ".");
    }

    // ===== Consultas =====
    public String getInfoCelular(int index) {
        if (index < 0 || index >= marcas.length) {
            return "Celular no encontrado";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(marcas[index]).append(" ").append(modelos[index]).append("\n\n");
        for (int j = 0; j < years_years; j++) {
            sb.append(years[j])
              .append(": $")
              .append(formatPrecio(precios[index][j]))
              .append("\n");
        }
        return sb.toString();
    }

    public String getMasBaratosPorYear_Year() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < years_years; j++) {
            int min = precios[0][j];
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
              .append(formatPrecio(min))
              .append(")\n");
        }
        return sb.toString();
    }

    public String getMasCarosPorYear_Year() {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < years_years; j++) {
            int max = precios[0][j];
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
              .append(formatPrecio(max))
              .append(")\n");
        }
        return sb.toString();
    }

    public String getPromedioYear_Year(int col) {
        long suma = 0;
        int cont = 0;
        for (int i = 0; i < marcas.length; i++) {
            suma += precios[i][col];
            cont++;
        }
        if (cont > 0) {
            int prom = (int)(suma / cont);
            return "$" + formatPrecio(prom);
        }
        return "No hay datos";
    }

    public String getPromedioRango(int col) {
        long suma = 0;
        int cont = 0;
        for (int i = 0; i < marcas.length; i++) {
            int p = precios[i][col];
            if (p >= 1500000 && p <= 3000000) {
                suma += p;
                cont++;
            }
        }
        if (cont > 0) {
            int prom = (int)(suma / cont);
            return "$" + formatPrecio(prom);
        } else {
            return "No hay datos en el rango";
        }
    }
}