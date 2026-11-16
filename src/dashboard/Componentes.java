package dashboard;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Componentes visuales reutilizables: botones planos, tarjetas y etiquetas.
 */
public final class Componentes {
    private Componentes() {
    }

    /**
     * Botón plano con esquinas redondeadas que se ve igual en cualquier sistema.
     */
    public static class Boton extends JButton {
        private final Color fondo;
        private final boolean relleno;
        private boolean encima;

        /**
         * @param texto texto del botón
         * @param fondo color principal
         * @param relleno true = fondo de color y texto blanco; false = contorno
         */
        public Boton(String texto, Color fondo, boolean relleno) {
            super(texto);
            this.fondo = fondo;
            this.relleno = relleno;
            setFont(Tema.TEXTO_NEGRITA);
            setForeground(relleno ? Color.WHITE : fondo);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    encima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    encima = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            Color base = isEnabled() ? fondo : Tema.GRIS_CLARO;
            if (relleno) {
                g2.setColor(encima && isEnabled() ? base.darker() : base);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            } else {
                g2.setColor(encima && isEnabled() ? Tema.aclarar(base, 0.9) : Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.setColor(Tema.aclarar(base, 0.45));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
            }
            setForeground(relleno ? Color.WHITE : base);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Panel blanco con borde y esquinas redondeadas, con título opcional.
     */
    public static class Tarjeta extends JPanel {
        /**
         * @param titulo título que aparece arriba, o null para no poner título
         */
        public Tarjeta(String titulo) {
            super(new BorderLayout(0, 8));
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
            if (titulo != null) {
                JLabel etiqueta = new JLabel(titulo);
                etiqueta.setFont(Tema.SUBTITULO);
                etiqueta.setForeground(Tema.TINTA);
                add(etiqueta, BorderLayout.NORTH);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(Tema.LINEA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
        }
    }

    /**
     * @param texto texto
     * @param fuente fuente
     * @param color color del texto
     * @return una etiqueta con el estilo dado
     */
    public static JLabel etiqueta(String texto, Font fuente, Color color) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente);
        etiqueta.setForeground(color);
        return etiqueta;
    }

    /**
     * @param columnas ancho en caracteres
     * @param texto texto inicial
     * @return un campo de texto con borde suave
     */
    public static JTextField campo(int columnas, String texto) {
        JTextField campo = new JTextField(texto, columnas);
        campo.setFont(Tema.TEXTO);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Tema.LINEA),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        return campo;
    }

    /**
     * @param componentes controles a acomodar en una fila
     * @return un panel transparente con los controles alineados a la izquierda
     */
    public static JPanel fila(JComponent... componentes) {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        fila.setOpaque(false);
        fila.setAlignmentX(0f);
        for (JComponent c : componentes) {
            fila.add(c);
        }
        return fila;
    }

    /**
     * @param ancho ancho
     * @param alto alto
     * @return un espacio vacío del tamaño dado
     */
    public static JComponent espacio(int ancho, int alto) {
        JPanel espacio = new JPanel();
        espacio.setOpaque(false);
        espacio.setPreferredSize(new Dimension(ancho, alto));
        return espacio;
    }
}
