package dashboard;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entrada de la versión web (GitHub Pages con CheerpJ). Es el mismo dashboard,
 * sin marco de ventana y pegado a la esquina para llenar la página.
 */
public final class Web {
    private Web() {
    }

    /**
     * @param args ancho y alto del área de la página (opcional)
     */
    public static void main(String[] args) {
        int ancho = args.length > 1 ? Integer.parseInt(args[0]) : 1380;
        int alto = args.length > 1 ? Integer.parseInt(args[1]) : 880;
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Se queda el look and feel por defecto.
            }
            Dashboard dashboard = new Dashboard();
            dashboard.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            dashboard.setUndecorated(true);
            dashboard.setBounds(0, 0, ancho, alto);
            dashboard.setVisible(true);
        });
    }
}
