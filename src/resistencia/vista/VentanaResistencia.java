package resistencia.vista;

import resistencia.modelo.Resistencia;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Ventana que muestra gráficamente los colores de una resistencia. */
public class VentanaResistencia extends JFrame {

    public VentanaResistencia(Resistencia resistencia) {
        super("Colores de la resistencia");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(new PanelResistencia(resistencia));
        pack();
        setLocationRelativeTo(null);
    }

    public static void mostrar(Resistencia resistencia) {
        SwingUtilities.invokeLater(() -> new VentanaResistencia(resistencia).setVisible(true));
    }
}
