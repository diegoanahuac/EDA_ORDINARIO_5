package dashboard;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Pantalla de inicio: una tarjeta por proyecto con un dibujo de su estructura.
 * Al hacer clic en una tarjeta se abre ese proyecto.
 */
public class PanelInicio extends JPanel {
    private static final String[] TITULOS = {
        "Resistencia", "Carrito de compras", "Navegador web",
        "Cola de impresión", "Sistema de archivos", "Ruta mágica"
    };
    private static final String[] ESTRUCTURAS = {
        "Arreglos", "Listas enlazadas", "Pilas (LIFO)",
        "Colas (FIFO)", "Árbol binario de búsqueda", "Grafo ponderado · Dijkstra"
    };
    private static final String[] DESCRIPCIONES = {
        "Código de colores y circuitos en serie y en paralelo",
        "Catálogo y carrito como listas de nodos enlazados",
        "Historial con dos pilas: atrás y adelante",
        "Cinco PCs envían trabajos a una impresora compartida",
        "Directorios y archivos en un ABB con sus recorridos",
        "Camino más corto entre los Pueblos Mágicos de Yucatán"
    };

    /**
     * @param dashboard ventana principal, para abrir el proyecto elegido
     */
    public PanelInicio(Dashboard dashboard) {
        super(new BorderLayout(0, 18));
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(26, 28, 26, 28));

        JComponent encabezado = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Tema.suavizar(g2);
                g2.setColor(Tema.TINTA);
                g2.setFont(Tema.TITULO.deriveFont(26f));
                g2.drawString("Proyecto Ordinario", 0, 30);
                g2.setFont(Tema.TEXTO);
                g2.setColor(Tema.GRIS);
                g2.drawString("Estructuras de Datos y Algoritmos · Lizbeth Hernández Olán · Universidad Anáhuac Mayab", 0, 54);
                g2.drawString("Aranza Aguilar Madrigal · Ariana Belén Chinchilla Salazar · Diego Kamil Olea Ruiz · Erwin Deangelo Torres", 0, 74);
                g2.dispose();
            }
        };
        encabezado.setPreferredSize(new java.awt.Dimension(100, 84));
        add(encabezado, BorderLayout.NORTH);

        JPanel rejilla = new JPanel(new GridLayout(2, 3, 18, 18));
        rejilla.setOpaque(false);
        for (int i = 0; i < 6; i++) {
            rejilla.add(new TarjetaProyecto(dashboard, i));
        }
        add(rejilla, BorderLayout.CENTER);
    }

    /** Tarjeta de un proyecto con su ilustración. */
    private static class TarjetaProyecto extends JComponent {
        private final int indice;
        private boolean encima;

        TarjetaProyecto(Dashboard dashboard, int indice) {
            this.indice = indice;
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    dashboard.mostrar(Dashboard.clave(indice + 1));
                }

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
            Color acento = Tema.ACENTOS[indice];
            int w = getWidth();
            int h = getHeight();
            g2.setColor(encima ? Tema.aclarar(acento, 0.94) : Color.WHITE);
            g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);
            g2.setColor(encima ? Tema.aclarar(acento, 0.4) : Tema.LINEA);
            g2.setStroke(new BasicStroke(encima ? 2f : 1f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

            // Zona de la ilustración.
            int alto = Math.max(110, h - 120);
            g2.setColor(Tema.aclarar(acento, 0.92));
            g2.fillRoundRect(10, 10, w - 21, alto, 14, 14);
            g2.translate(10, 10);
            dibujarIlustracion(g2, indice, w - 21, alto, acento);
            g2.translate(-10, -10);

            int y = alto + 34;
            g2.setColor(acento);
            g2.fillOval(16, y - 17, 24, 24);
            g2.setColor(Color.WHITE);
            g2.setFont(Tema.TEXTO_NEGRITA);
            Tema.centrado(g2, String.valueOf(indice + 1), 28, y);
            g2.setColor(Tema.TINTA);
            g2.setFont(Tema.SUBTITULO);
            g2.drawString(TITULOS[indice], 48, y);
            g2.setFont(Tema.PEQUENA_NEGRITA);
            g2.setColor(acento);
            g2.drawString(ESTRUCTURAS[indice].toUpperCase(), 16, y + 22);
            g2.setFont(Tema.TEXTO);
            g2.setColor(Tema.GRIS);
            g2.drawString(Tema.recortar(g2, DESCRIPCIONES[indice], w - 32), 16, y + 42);
            g2.setFont(Tema.TEXTO_NEGRITA);
            g2.setColor(acento);
            g2.drawString("Abrir proyecto  →", 16, y + 66);
            g2.dispose();
        }
    }

    /**
     * Dibuja la ilustración de la estructura de cada proyecto.
     */
    private static void dibujarIlustracion(Graphics2D g, int indice, int w, int h, Color acento) {
        int cx = w / 2;
        int cy = h / 2;
        g.setStroke(new BasicStroke(2f));
        switch (indice) {
            case 0: { // Resistencia
                g.setColor(Tema.GRIS);
                g.drawLine(cx - 120, cy, cx - 60, cy);
                g.drawLine(cx + 60, cy, cx + 120, cy);
                g.setColor(new Color(0xE8D3A9));
                g.fillRoundRect(cx - 62, cy - 20, 124, 40, 26, 26);
                Color[] bandas = { new Color(0, 140, 0), new Color(128, 128, 128), new Color(200, 20, 20), new Color(0xC9A227) };
                int[] xs = { -38, -18, 2, 34 };
                for (int i = 0; i < 4; i++) {
                    g.setColor(bandas[i]);
                    g.fillRect(cx + xs[i], cy - 20, 10, 40);
                }
                g.setColor(acento);
                g.setFont(Tema.TEXTO_NEGRITA);
                Tema.centrado(g, "5 800 Ω", cx, cy + 44);
                break;
            }
            case 1: { // Lista enlazada
                int n = 3;
                int ancho = 64;
                int x0 = cx - (n * ancho + (n - 1) * 26) / 2;
                for (int i = 0; i < n; i++) {
                    int x = x0 + i * (ancho + 26);
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(x, cy - 18, ancho, 36, 8, 8);
                    g.setColor(acento);
                    g.drawRoundRect(x, cy - 18, ancho, 36, 8, 8);
                    g.drawLine(x + ancho - 16, cy - 18, x + ancho - 16, cy + 18);
                    g.fillOval(x + ancho - 11, cy - 3, 6, 6);
                    flecha(g, x + ancho - 8, cy, x + ancho + 24, cy);
                }
                g.setFont(Tema.PEQUENA_NEGRITA);
                g.drawString("null", x0 + n * (ancho + 26) - 2, cy + 4);
                break;
            }
            case 2: { // Pilas
                for (int p = 0; p < 2; p++) {
                    int x = cx - 90 + p * 110;
                    int bloques = p == 0 ? 3 : 2;
                    for (int i = 0; i < bloques; i++) {
                        int y = cy + 30 - (i + 1) * 22;
                        g.setColor(i == bloques - 1 ? acento : Tema.aclarar(acento, 0.55));
                        g.fillRoundRect(x, y, 70, 18, 6, 6);
                    }
                    g.setColor(Tema.GRIS);
                    g.drawLine(x - 4, cy - 40, x - 4, cy + 32);
                    g.drawLine(x + 74, cy - 40, x + 74, cy + 32);
                    g.drawLine(x - 4, cy + 32, x + 74, cy + 32);
                }
                break;
            }
            case 3: { // Cola
                for (int i = 0; i < 5; i++) {
                    int x = cx - 120 + i * 44;
                    g.setColor(i < 4 ? acento : Tema.aclarar(acento, 0.7));
                    g.fillRoundRect(x, cy - 16, 38, 32, 8, 8);
                    g.setColor(Color.WHITE);
                    g.setFont(Tema.PEQUENA_NEGRITA);
                    if (i < 4) {
                        Tema.centrado(g, "PC" + (i + 2), x + 19, cy + 4);
                    }
                }
                g.setColor(Tema.GRIS);
                flecha(g, cx + 104, cy, cx + 136, cy);
                g.setColor(Tema.TINTA);
                g.fillRoundRect(cx + 140, cy - 18, 34, 30, 6, 6);
                g.setColor(Color.WHITE);
                g.fillRect(cx + 146, cy + 8, 22, 14);
                break;
            }
            case 4: { // Árbol
                int[][] nodos = { { cx, cy - 34 }, { cx - 70, cy + 4 }, { cx + 70, cy + 4 },
                    { cx - 100, cy + 40 }, { cx - 40, cy + 40 }, { cx + 100, cy + 40 } };
                int[][] aristas = { { 0, 1 }, { 0, 2 }, { 1, 3 }, { 1, 4 }, { 2, 5 } };
                g.setColor(Tema.aclarar(acento, 0.4));
                for (int[] a : aristas) {
                    g.drawLine(nodos[a[0]][0], nodos[a[0]][1], nodos[a[1]][0], nodos[a[1]][1]);
                }
                for (int i = 0; i < nodos.length; i++) {
                    g.setColor(i < 3 ? new Color(0xF4C152) : Color.WHITE);
                    g.fillRoundRect(nodos[i][0] - 22, nodos[i][1] - 12, 44, 24, 6, 6);
                    g.setColor(acento);
                    g.drawRoundRect(nodos[i][0] - 22, nodos[i][1] - 12, 44, 24, 6, 6);
                }
                break;
            }
            default: { // Grafo
                int[][] p = { { cx - 110, cy - 30 }, { cx - 40, cy - 38 }, { cx + 10, cy - 4 },
                    { cx + 100, cy - 26 }, { cx + 110, cy + 26 }, { cx - 60, cy + 34 }, { cx - 20, cy + 46 } };
                int[][] e = { { 0, 1 }, { 1, 2 }, { 1, 3 }, { 2, 3 }, { 2, 4 }, { 3, 4 }, { 0, 5 }, { 5, 2 }, { 5, 6 }, { 6, 4 } };
                for (int[] a : e) {
                    boolean ruta = (a[0] == 0 && a[1] == 1) || (a[0] == 1 && a[1] == 2) || (a[0] == 2 && a[1] == 3);
                    g.setColor(ruta ? Tema.VERDE : Tema.aclarar(acento, 0.55));
                    g.setStroke(new BasicStroke(ruta ? 3.5f : 1.6f));
                    g.drawLine(p[a[0]][0], p[a[0]][1], p[a[1]][0], p[a[1]][1]);
                }
                for (int[] q : p) {
                    g.setColor(Color.WHITE);
                    g.fillOval(q[0] - 8, q[1] - 8, 16, 16);
                    g.setColor(acento);
                    g.setStroke(new BasicStroke(2f));
                    g.drawOval(q[0] - 8, q[1] - 8, 16, 16);
                }
            }
        }
    }

    /**
     * Dibuja una flecha de (x1, y1) a (x2, y2).
     *
     * @param g contexto gráfico
     */
    public static void flecha(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        Path2D punta = new Path2D.Double();
        punta.moveTo(x2, y2);
        punta.lineTo(x2 - 9 * Math.cos(angulo - 0.45), y2 - 9 * Math.sin(angulo - 0.45));
        punta.lineTo(x2 - 9 * Math.cos(angulo + 0.45), y2 - 9 * Math.sin(angulo + 0.45));
        punta.closePath();
        g.fill(punta);
    }
}
