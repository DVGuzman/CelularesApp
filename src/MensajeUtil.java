import javax.swing.*;
import java.awt.Component;

public class MensajeUtil {
    public static void mostrarError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void mostrarInfo(Component parent, String msg, String titulo) {
        JOptionPane.showMessageDialog(parent,
                msg,
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
