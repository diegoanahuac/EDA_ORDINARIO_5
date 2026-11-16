package dashboard.proyectos;

import dashboard.Componentes;
import dashboard.Componentes.Boton;
import dashboard.Componentes.Tarjeta;
import dashboard.PanelProyecto;
import dashboard.Tema;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import navegador.modelo.Navegador;
import navegador.modelo.Pagina;
import navegador.modelo.Pila;
import navegador.modelo.Resultado;

/**
 * Proyecto 3. Navegador web simulado con dos pilas (atrás y adelante), usando
 * las clases Navegador y Pila del equipo. Se ven la página actual y las dos
 * pilas con su cima en todo momento.
 */
public class ProyectoNavegador extends PanelProyecto {
    private final Runnable alSalir;
    private Navegador navegador = new Navegador();
    private final JComboBox<String> comboUrl = new JComboBox<>();
    private final Boton botonAtras;
    private final Boton botonAdelante;
    private final LienzoPagina lienzoPagina = new LienzoPagina();
    private final LienzoPilas lienzoPilas = new LienzoPilas();
    private final List<String> bitacora = new ArrayList<>();

    /**
     * @param alSalir acción al presionar Exit (regresar al inicio del dashboard)
     */
    public ProyectoNavegador(Runnable alSalir) {
        super(3, "Navegador web", "Pilas (LIFO)",
                "Visitar guarda la página actual en la pila atrás y vacía la pila adelante. Back y Forward mueven páginas entre pilas.");
        this.alSalir = alSalir;

        comboUrl.setEditable(true);
        comboUrl.setFont(Tema.TEXTO);
        comboUrl.setPreferredSize(new Dimension(380, 34));
        for (String url : navegador.getCatalogo().urlsDisponibles()) {
            comboUrl.addItem(url);
        }
        comboUrl.addItem("https://www.pagina-que-no-existe.com");

        botonAtras = new Boton("←  Back", acento, false);
        botonAdelante = new Boton("Forward  →", acento, false);
        Boton botonVisitar = new Boton("Visitar", acento, true);
        Boton botonSalir = new Boton("Exit", Tema.ROJO, false);
        botonAtras.addActionListener(e -> aplicar(navegador.atras(), "Back"));
        botonAdelante.addActionListener(e -> aplicar(navegador.adelante(), "Forward"));
        botonVisitar.addActionListener(e -> visitar(String.valueOf(comboUrl.getEditor().getItem())));
        comboUrl.getEditor().addActionListener(e -> visitar(String.valueOf(comboUrl.getEditor().getItem())));
        botonSalir.addActionListener(e -> salir());

        Tarjeta barra = new Tarjeta(null);
        barra.add(Componentes.fila(botonAtras, botonAdelante, comboUrl, botonVisitar, botonSalir), BorderLayout.CENTER);

        Tarjeta tarjetaPagina = new Tarjeta("Página actual");
        tarjetaPagina.add(lienzoPagina, BorderLayout.CENTER);
        Tarjeta tarjetaPilas = new Tarjeta("Historial: dos pilas de capacidad " + Pila.CAPACIDAD_POR_DEFECTO);
        tarjetaPilas.add(lienzoPilas, BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(1, 2, 14, 0));
        centro.setOpaque(false);
        centro.add(tarjetaPagina);
        centro.add(tarjetaPilas);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 14));
        cuerpo.setOpaque(false);
        cuerpo.add(barra, BorderLayout.NORTH);
        cuerpo.add(centro, BorderLayout.CENTER);
        setContenido(cuerpo);
        actualizarBotones();
    }

    @Override
    public void cargarEjemplo() {
        navegador = new Navegador();
        bitacora.clear();
        visitar("https://www.google.com");
        visitar("https://merida.anahuac.mx");
        visitar("https://github.com");
        visitar("https://stackoverflow.com");
        aplicar(navegador.atras(), "Back");
    }

    /**
     * Visita una URL como si se escribiera en la barra de direcciones.
     *
     * @param url dirección a visitar
     */
    public void visitar(String url) {
        aplicar(navegador.visitar(url), "Visitar " + url);
    }

    /** Muestra el resultado de la operación y redibuja. */
    private void aplicar(Resultado resultado, String operacion) {
        TipoMensaje tipo;
        switch (resultado.getTipo()) {
            case EXITO:
                tipo = TipoMensaje.EXITO;
                break;
            case AVISO:
                tipo = TipoMensaje.AVISO;
                break;
            default:
                tipo = TipoMensaje.ERROR;
        }
        mensaje(resultado.getMensaje(), tipo);
        bitacora.add(0, operacion + (tipo == TipoMensaje.ERROR ? "  (sin cambios)" : ""));
        while (bitacora.size() > 6) {
            bitacora.remove(bitacora.size() - 1);
        }
        actualizarBotones();
        lienzoPagina.repaint();
        lienzoPilas.repaint();
    }

    private void actualizarBotones() {
        botonAtras.setEnabled(navegador.puedeRetroceder());
        botonAdelante.setEnabled(navegador.puedeAvanzar());
    }

    private void salir() {
        int r = JOptionPane.showConfirmDialog(this, "¿Salir del navegador? Se borrará el historial.",
                "Exit", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            navegador = new Navegador();
            bitacora.clear();
            actualizarBotones();
            repaint();
            mensaje("Saliste del navegador. El historial se borró.", TipoMensaje.INFO);
            alSalir.run();
        }
    }

    /** Ventana de navegador simulada con la página actual. */
    private class LienzoPagina extends JComponent {
        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            int w = getWidth() - 2;
            int h = getHeight() - 2;
            Pagina pagina = navegador.getPaginaActual();

            g.setColor(Tema.GRIS_SUAVE);
            g.fillRoundRect(0, 0, w, h, 14, 14);
            g.setColor(Tema.LINEA);
            g.drawRoundRect(0, 0, w, h, 14, 14);
            Color[] puntos = { new Color(0xF04438), new Color(0xF79009), new Color(0x12B76A) };
            for (int i = 0; i < 3; i++) {
                g.setColor(puntos[i]);
                g.fillOval(14 + i * 18, 14, 11, 11);
            }
            g.setColor(Color.WHITE);
            g.fillRoundRect(80, 8, 220, 24, 8, 8);
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.setColor(Tema.TINTA);
            g.drawString(Tema.recortar(g, pagina == null ? "Nueva pestaña" : pagina.getTitulo(), 200), 92, 25);

            // Barra de direcciones.
            g.setColor(Color.WHITE);
            g.fillRoundRect(12, 42, w - 24, 30, 15, 15);
            g.setFont(Tema.TEXTO_NEGRITA);
            g.setColor(navegador.puedeRetroceder() ? acento : Tema.LINEA);
            g.drawString("←", 26, 62);
            g.setColor(navegador.puedeAvanzar() ? acento : Tema.LINEA);
            g.drawString("→", 48, 62);
            g.setFont(Tema.TEXTO);
            g.setColor(Tema.TINTA);
            g.drawString(Tema.recortar(g, pagina == null ? "Escribe una URL" : pagina.getUrl(), w - 110), 76, 62);

            // Contenido.
            int top = 84;
            g.setColor(Color.WHITE);
            g.fillRoundRect(12, top, w - 24, h - top - 12, 10, 10);
            if (pagina == null) {
                g.setColor(Tema.GRIS);
                g.setFont(Tema.SUBTITULO);
                Tema.centrado(g, "Escribe una URL y presiona Visitar", w / 2, top + (h - top) / 2);
            } else if (!pagina.fueEncontrada()) {
                g.setColor(Tema.ROJO);
                g.setFont(Tema.TITULO.deriveFont(64f));
                Tema.centrado(g, "404", w / 2, top + (h - top) / 2 - 10);
                g.setFont(Tema.TEXTO);
                g.setColor(Tema.GRIS);
                Tema.centrado(g, "La página no existe en el catálogo local.", w / 2, top + (h - top) / 2 + 24);
            } else {
                Color tono = Tema.ACENTOS[Math.abs(pagina.getUrl().hashCode()) % Tema.ACENTOS.length];
                g.setColor(Tema.aclarar(tono, 0.85));
                g.fillRoundRect(28, top + 18, w - 56, 110, 12, 12);
                g.setColor(tono);
                g.setFont(Tema.TITULO.deriveFont(28f));
                g.drawString(Tema.recortar(g, pagina.getTitulo(), w - 100), 48, top + 66);
                g.setFont(Tema.TEXTO);
                g.setColor(Tema.TINTA);
                g.drawString(Tema.recortar(g, pagina.getDescripcion(), w - 100), 48, top + 96);
                g.setColor(Tema.GRIS_SUAVE);
                for (int i = 0; i < 4; i++) {
                    int ancho = (w - 56) - i * 60;
                    g.fillRoundRect(28, top + 150 + i * 26, Math.max(80, ancho), 12, 6, 6);
                }
            }
            g.dispose();
        }
    }

    /** Dibujo de las dos pilas y la bitácora de operaciones. */
    private class LienzoPilas extends JComponent {
        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            int w = getWidth();
            int columna = (w - 30) / 2;
            dibujarPila(g, navegador.getPilaAtras(), "Pila atrás (Back)", 0, columna);
            dibujarPila(g, navegador.getPilaAdelante(), "Pila adelante (Forward)", columna + 30, columna);

            int y = 334;
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.setColor(Tema.GRIS);
            g.drawString("ÚLTIMAS OPERACIONES", 0, y);
            g.setFont(Tema.PEQUENA);
            for (int i = 0; i < bitacora.size(); i++) {
                g.setColor(i == 0 ? Tema.TINTA : Tema.GRIS);
                g.drawString((i == 0 ? "▶ " : "   ") + bitacora.get(i), 0, y + 18 + i * 17);
            }
            g.dispose();
        }

        private void dibujarPila(Graphics2D g, Pila<Pagina> pila, String titulo, int x, int ancho) {
            List<Pagina> elementos = pila.elementosDesdeElFondo();
            int capacidad = pila.capacidad();
            int alto = 42;
            int base = 296;
            g.setFont(Tema.TEXTO_NEGRITA);
            g.setColor(Tema.TINTA);
            g.drawString(titulo, x, 14);
            g.setFont(Tema.PEQUENA);
            g.setColor(Tema.GRIS);
            g.drawString(pila.tamano() + " / " + capacidad + (pila.estaVacia() ? "  · vacía" : ""), x, 30);

            g.setColor(Tema.GRIS_CLARO);
            g.setStroke(new BasicStroke(2.5f));
            g.drawLine(x, base - capacidad * (alto + 6) - 4, x, base + 4);
            g.drawLine(x + ancho, base - capacidad * (alto + 6) - 4, x + ancho, base + 4);
            g.drawLine(x, base + 4, x + ancho, base + 4);

            for (int i = 0; i < capacidad; i++) {
                int y = base - (i + 1) * (alto + 6) + 4;
                if (i < elementos.size()) {
                    Pagina p = elementos.get(i);
                    boolean cima = i == elementos.size() - 1;
                    g.setColor(cima ? acento : Tema.aclarar(acento, 0.75));
                    g.fillRoundRect(x + 8, y, ancho - 16, alto, 8, 8);
                    g.setColor(cima ? Color.WHITE : Tema.TINTA);
                    g.setFont(Tema.TEXTO_NEGRITA);
                    g.drawString(Tema.recortar(g, p.getTitulo(), ancho - 70), x + 18, y + 18);
                    g.setFont(Tema.PEQUENA);
                    g.drawString(Tema.recortar(g, p.getUrl(), ancho - 40), x + 18, y + 34);
                    if (cima) {
                        g.setFont(Tema.PEQUENA_NEGRITA);
                        g.drawString("cima", x + ancho - 46, y + 18);
                    }
                } else {
                    g.setColor(Tema.LINEA);
                    g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                            new float[] { 5f, 4f }, 0f));
                    g.drawRoundRect(x + 8, y, ancho - 16, alto, 8, 8);
                    g.setStroke(new BasicStroke(1f));
                }
            }
        }
    }
}
