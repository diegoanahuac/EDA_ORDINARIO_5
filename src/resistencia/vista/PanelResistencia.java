package resistencia.vista;

import resistencia.modelo.ColorResistencia;
import resistencia.modelo.Resistencia;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/** Dibuja el cuerpo de una resistencia con sus bandas de color. */
public class PanelResistencia extends JPanel {

    private final Resistencia resistencia;

    public PanelResistencia(Resistencia resistencia) {
        this.resistencia = resistencia;
        setPreferredSize(new Dimension(420, 220));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cuerpoAncho = 240;
        int cuerpoAlto = 70;
        int cuerpoX = (getWidth() - cuerpoAncho) / 2;
        int cuerpoY = (getHeight() - cuerpoAlto) / 2;

        g2.setColor(Color.GRAY);
        g2.fillRect(cuerpoX - 60, cuerpoY + cuerpoAlto / 2 - 3, 60, 6);
        g2.fillRect(cuerpoX + cuerpoAncho, cuerpoY + cuerpoAlto / 2 - 3, 60, 6);

        g2.setColor(new Color(222, 184, 135));
        g2.fillRoundRect(cuerpoX, cuerpoY, cuerpoAncho, cuerpoAlto, 35, 35);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(cuerpoX, cuerpoY, cuerpoAncho, cuerpoAlto, 35, 35);

        ColorResistencia[] bandas = resistencia.obtenerBandas();
        int bandaAncho = 20;
        int espacio = 45;
        int primeraX = cuerpoX + 45;
        for (int i = 0; i < bandas.length; i++) {
            g2.setColor(bandas[i].getColorAwt());
            int x = primeraX + i * espacio;
            g2.fillRect(x, cuerpoY + 3, bandaAncho, cuerpoAlto - 6);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect(x, cuerpoY + 3, bandaAncho, cuerpoAlto - 6);
        }

        g2.setColor(Color.BLACK);
        String etiqueta = resistencia.getValorOhmios() + " ohmios ("
                + bandas[0] + ", " + bandas[1] + ", " + bandas[2] + ")";
        g2.drawString(etiqueta, cuerpoX - 20, cuerpoY + cuerpoAlto + 35);
    }
}
