package dashboard;

import dashboard.clase.PanelCodigoClase;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Base de cada proyecto del dashboard: encabezado con número, título,
 * estructura de datos y descripción; área de contenido; barra de mensajes.
 */
public abstract class PanelProyecto extends JPanel {
    /** Tipo de mensaje de la barra inferior. */
    public enum TipoMensaje { INFO, EXITO, AVISO, ERROR }

    private final int numero;
    private final String titulo;
    private final String estructura;
    private final String descripcion;
    protected final Color acento;
    private final JLabel barraMensaje = new JLabel();
    private TipoMensaje tipoActual = TipoMensaje.INFO;
    private static final String VISTA_DEMO = "demo";
    private static final String VISTA_CLASE = "clase";
    private final CardLayout cartas = new CardLayout();
    private JToggleButton selectorDemo;
    private JToggleButton selectorClase;

    /**
     * @param numero número del proyecto (1 a 6)
     * @param titulo nombre del proyecto
     * @param estructura estructura de datos principal
     * @param descripcion una línea que explica qué hace
     */
    protected PanelProyecto(int numero, String titulo, String estructura, String descripcion) {
        super(new BorderLayout(0, 12));
        this.numero = numero;
        this.titulo = titulo;
        this.estructura = estructura;
        this.descripcion = descripcion;
        this.acento = Tema.ACENTOS[numero - 1];
        setBackground(Tema.FONDO);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 14, 22));
        add(new Encabezado(), BorderLayout.NORTH);

        JPanel barra = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Tema.suavizar(g2);
                g2.setColor(fondoMensaje());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        barra.setOpaque(false);
        barra.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        barraMensaje.setFont(Tema.TEXTO);
        barra.add(barraMensaje, BorderLayout.CENTER);
        add(barra, BorderLayout.SOUTH);
        mensaje("Listo.", TipoMensaje.INFO);
    }

    /** @return el número del proyecto */
    public int getNumero() {
        return numero;
    }

    /** @return el nombre del proyecto */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Coloca el contenido principal del proyecto.
     *
     * @param contenido componente central
     */
    protected void setContenido(JComponent contenido) {
        // Dos vistas por proyecto: la demo interactiva y la explicación de las
        // funciones con lo visto en clase (rama explicacion-clase).
        JPanel vistas = new JPanel(cartas);
        vistas.setOpaque(false);
        vistas.add(contenido, VISTA_DEMO);
        vistas.add(new PanelCodigoClase(numero, acento), VISTA_CLASE);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(selectorVista(vistas), BorderLayout.NORTH);
        centro.add(vistas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    /**
     * Cambia entre la demo interactiva y la pestaña "Código y clase".
     *
     * @param clase true para mostrar el código y su relación con la clase
     */
    public void mostrarCodigoClase(boolean clase) {
        if (selectorClase != null) {
            (clase ? selectorClase : selectorDemo).doClick();
        }
    }

    private JComponent selectorVista(JPanel vistas) {
        selectorDemo = new JToggleButton("▶  Demo interactiva", true);
        selectorClase = new JToggleButton("{ }  Código y clase");
        ButtonGroup grupo = new ButtonGroup();
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        fila.setOpaque(false);
        for (JToggleButton b : new JToggleButton[] {selectorDemo, selectorClase}) {
            grupo.add(b);
            // UI básica para que respete el color de fondo con cualquier look and feel.
            b.setUI(new javax.swing.plaf.basic.BasicToggleButtonUI());
            b.setFont(Tema.TEXTO_NEGRITA);
            b.setFocusPainted(false);
            b.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
            b.setOpaque(true);
            // Holgura: con otras fuentes (versión web) el texto mide un poco más.
            Dimension medida = b.getPreferredSize();
            b.setPreferredSize(new Dimension(medida.width + 20, medida.height));
            b.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            fila.add(b);
        }
        Runnable pintar = () -> {
            for (JToggleButton b : new JToggleButton[] {selectorDemo, selectorClase}) {
                b.setBackground(b.isSelected() ? acento : Color.WHITE);
                b.setForeground(b.isSelected() ? Color.WHITE : Tema.GRIS);
            }
        };
        selectorDemo.addActionListener(e -> {
            cartas.show(vistas, VISTA_DEMO);
            pintar.run();
        });
        selectorClase.addActionListener(e -> {
            cartas.show(vistas, VISTA_CLASE);
            pintar.run();
        });
        pintar.run();
        fila.setBorder(BorderFactory.createLineBorder(Tema.LINEA));
        JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        envoltura.setOpaque(false);
        envoltura.add(fila);
        return envoltura;
    }

    /**
     * Muestra un mensaje en la barra inferior con el color de su tipo.
     *
     * @param texto mensaje
     * @param tipo tipo de mensaje
     */
    public void mensaje(String texto, TipoMensaje tipo) {
        tipoActual = tipo;
        String icono;
        switch (tipo) {
            case EXITO:
                icono = "✔  ";
                break;
            case AVISO:
                icono = "⚠  ";
                break;
            case ERROR:
                icono = "✖  ";
                break;
            default:
                icono = "ℹ  ";
        }
        barraMensaje.setText(icono + texto);
        barraMensaje.setForeground(colorMensaje());
        repaint();
    }

    private Color colorMensaje() {
        switch (tipoActual) {
            case EXITO:
                return Tema.VERDE;
            case AVISO:
                return Tema.AMBAR;
            case ERROR:
                return Tema.ROJO;
            default:
                return Tema.AZUL;
        }
    }

    private Color fondoMensaje() {
        switch (tipoActual) {
            case EXITO:
                return Tema.VERDE_SUAVE;
            case AVISO:
                return Tema.AMBAR_SUAVE;
            case ERROR:
                return Tema.ROJO_SUAVE;
            default:
                return Tema.AZUL_SUAVE;
        }
    }

    /** Pone el proyecto en un estado de ejemplo para mostrarlo rápido. */
    public abstract void cargarEjemplo();

    /** Encabezado dibujado: círculo con el número, título, etiqueta y descripción. */
    private class Encabezado extends JComponent {
        Encabezado() {
            setPreferredSize(new Dimension(100, 62));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(acento);
            g2.fillOval(0, 6, 48, 48);
            g2.setColor(Color.WHITE);
            g2.setFont(Tema.TITULO);
            Tema.centrado(g2, String.valueOf(numero), 24, 38);

            g2.setColor(Tema.TINTA);
            g2.setFont(Tema.TITULO);
            g2.drawString(titulo, 64, 28);
            int anchoTitulo = g2.getFontMetrics().stringWidth(titulo);

            g2.setFont(Tema.PEQUENA_NEGRITA);
            int anchoChip = g2.getFontMetrics().stringWidth(estructura) + 20;
            int xChip = 64 + anchoTitulo + 12;
            g2.setColor(Tema.aclarar(acento, 0.85));
            g2.fillRoundRect(xChip, 10, anchoChip, 22, 22, 22);
            g2.setColor(acento);
            g2.drawString(estructura, xChip + 10, 25);

            g2.setFont(Tema.TEXTO);
            g2.setColor(Tema.GRIS);
            g2.drawString(descripcion, 64, 52);
            g2.dispose();
        }
    }
}
