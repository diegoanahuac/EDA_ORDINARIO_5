package dashboard;

import dashboard.proyectos.ProyectoArchivos;
import dashboard.proyectos.ProyectoCarrito;
import dashboard.proyectos.ProyectoCola;
import dashboard.proyectos.ProyectoNavegador;
import dashboard.proyectos.ProyectoResistencia;
import dashboard.proyectos.ProyectoRutaMagica;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Ventana principal del Proyecto Ordinario. Tiene una barra lateral con los
 * seis proyectos y un área central donde se muestra cada uno por separado.
 */
public class Dashboard extends JFrame {
    /** Clave de la pantalla de inicio. */
    public static final String INICIO = "inicio";

    private final CardLayout tarjetas = new CardLayout();
    private final JPanel contenido = new JPanel(tarjetas);
    private final List<PanelProyecto> proyectos = new ArrayList<>();
    private final List<ElementoMenu> menu = new ArrayList<>();
    private String actual = INICIO;

    /** Construye la ventana con los seis proyectos. */
    public Dashboard() {
        super("EDA · Proyecto Ordinario · Equipo 5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 760));
        setSize(1380, 880);
        setLocationRelativeTo(null);

        proyectos.add(new ProyectoResistencia());
        proyectos.add(new ProyectoCarrito());
        proyectos.add(new ProyectoNavegador(() -> mostrar(INICIO)));
        proyectos.add(new ProyectoCola());
        proyectos.add(new ProyectoArchivos());
        proyectos.add(new ProyectoRutaMagica());

        contenido.add(new PanelInicio(this), INICIO);
        for (PanelProyecto p : proyectos) {
            contenido.add(p, clave(p.getNumero()));
        }

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.add(crearLateral(), BorderLayout.WEST);
        raiz.add(contenido, BorderLayout.CENTER);
        setContentPane(raiz);
        mostrar(INICIO);
    }

    /**
     * @param numero número del proyecto
     * @return la clave de su tarjeta en el CardLayout
     */
    public static String clave(int numero) {
        return "proyecto" + numero;
    }

    /** @return los paneles de los seis proyectos */
    public List<PanelProyecto> getProyectos() {
        return proyectos;
    }

    /**
     * Cambia la pantalla visible.
     *
     * @param clave INICIO o la clave de un proyecto
     */
    public void mostrar(String clave) {
        actual = clave;
        tarjetas.show(contenido, clave);
        for (ElementoMenu e : menu) {
            e.repaint();
        }
    }

    private JComponent crearLateral() {
        JPanel lateral = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Tema.suavizar(g2);
                g2.setColor(Tema.LATERAL);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(Tema.SUBTITULO.deriveFont(18f));
                g2.drawString("EDA · Ordinario", 22, 40);
                g2.setFont(Tema.PEQUENA);
                g2.setColor(Tema.GRIS_CLARO);
                g2.drawString("Equipo ProyectoFinal 5", 22, 60);
                g2.drawString("Estructuras de Datos y Algoritmos", 22, getHeight() - 38);
                g2.drawString("Anáhuac Mayab · 202660", 22, getHeight() - 22);
                g2.dispose();
            }
        };
        lateral.setLayout(new BorderLayout());
        lateral.setPreferredSize(new Dimension(236, 100));
        lateral.setBorder(BorderFactory.createEmptyBorder(84, 10, 0, 10));
        JPanel lista = new JPanel(new GridLayout(0, 1, 0, 2));
        lista.setOpaque(false);
        lateral.add(lista, BorderLayout.NORTH);

        agregarMenu(lista, new ElementoMenu(INICIO, "⌂", "Inicio", "Resumen de los 6 proyectos", Color.WHITE));
        String[][] datos = {
            { "Resistencia", "Arreglos" },
            { "Carrito de compras", "Listas enlazadas" },
            { "Navegador web", "Pilas (LIFO)" },
            { "Cola de impresión", "Colas (FIFO)" },
            { "Sistema de archivos", "Árbol binario de búsqueda" },
            { "Ruta mágica", "Grafos · Dijkstra" }
        };
        for (int i = 0; i < datos.length; i++) {
            agregarMenu(lista, new ElementoMenu(clave(i + 1), String.valueOf(i + 1),
                    datos[i][0], datos[i][1], Tema.ACENTOS[i]));
        }
        return lateral;
    }

    private void agregarMenu(JPanel lateral, ElementoMenu elemento) {
        menu.add(elemento);
        lateral.add(elemento);
    }

    /** Elemento de la barra lateral: círculo con número, nombre y estructura. */
    private class ElementoMenu extends JComponent {
        private final String destino;
        private final String marca;
        private final String nombre;
        private final String detalle;
        private final Color color;
        private boolean encima;

        ElementoMenu(String destino, String marca, String nombre, String detalle, Color color) {
            this.destino = destino;
            this.marca = marca;
            this.nombre = nombre;
            this.detalle = detalle;
            this.color = color;
            setPreferredSize(new Dimension(216, 58));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mostrar(destino);
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
            boolean activo = destino.equals(actual);
            if (activo || encima) {
                g2.setColor(activo ? Tema.LATERAL_ACTIVO : new Color(0x172036));
                g2.fillRoundRect(0, 4, getWidth(), getHeight() - 8, 12, 12);
            }
            if (activo) {
                g2.setColor(color.equals(Color.WHITE) ? Tema.AZUL_MEDIO : Tema.aclarar(color, 0.2));
                g2.fillRoundRect(0, 14, 4, getHeight() - 28, 4, 4);
            }
            Color circulo = color.equals(Color.WHITE) ? Tema.AZUL_MEDIO : color;
            g2.setColor(circulo);
            g2.fillOval(14, 14, 30, 30);
            g2.setColor(Color.WHITE);
            g2.setFont(Tema.TEXTO_NEGRITA);
            Tema.centrado(g2, marca, 29, 34);
            g2.setFont(Tema.TEXTO_NEGRITA);
            g2.setColor(activo ? Color.WHITE : new Color(0xE4E7EC));
            g2.drawString(nombre, 54, 27);
            g2.setFont(Tema.PEQUENA);
            g2.setColor(Tema.GRIS_CLARO);
            g2.drawString(detalle, 54, 43);
            g2.dispose();
        }
    }

    /**
     * Punto de entrada del dashboard.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Si no se puede cambiar, se queda el look and feel por defecto.
            }
            new Dashboard().setVisible(true);
        });
    }
}
