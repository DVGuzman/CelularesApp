import java.util.regex.Pattern;

public class ValidacionUtil {
    private static final Pattern TEXTO_VALIDO = Pattern.compile("^[a-zA-Z0-9 ]+$");

    public static void validarTexto(String texto, String campo, int fila) throws Exception {
        if (texto.isEmpty())
            throw new Exception(campo + " vacío en fila " + (fila + 1));

        if (!TEXTO_VALIDO.matcher(texto).matches())
            throw new Exception(campo + " inválido en fila " + (fila + 1));
    }
}
