package dashboard.proyectos;

import cola.ColaImpresion;
import dashboard.Componentes;
import dashboard.Componentes.Boton;
import dashboard.Componentes.Tarjeta;
import dashboard.PanelInicio;
import dashboard.PanelProyecto;
import dashboard.Tema;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * Proyecto 4. Cola de impresión FIFO con capacidad 6. Las cinco computadoras
 * envían trabajos al final de la cola y la impresora atiende el frente.
 * Usa la clase ColaImpresion del equipo.
 */
public class ProyectoCola extends PanelProyecto {
    private static final Color[] COLORES_PC = {
        new Color(0x2563EB), new Color(0x15803D), new Color(0xB54708), new Color(0x6941C6), new Color(0xC11574)
    };

    private ColaImpresion cola = new ColaImpresion();
    private final List<String> impresos = new ArrayList<>();
    private final LienzoCola lienzo = new LienzoCola();
    private final Timer temporizador;
    private String ultimoEmisor;
    private String alerta;
    private String animacionPc;
    private boolean animacionEntra;
    private double progreso = 1;

    /** Construye el panel del proyecto 4. */
    public ProyectoCola() {
        super(4, "Cola de impresión", "Colas (FIFO)",
                "Los trabajos entran por el final y la impresora atiende el frente: el primero que llega es el primero que se imprime.");
        temporizador = new Timer(15, e -> avanzarAnimacion());

        Boton botonAgregar = new Boton("Agregar trabajo (PC al azar)", acento, true);
        botonAgregar.addActionListener(e -> agregar(null));
        Boton botonImprimir = new Boton("Imprimir", Tema.TINTA, true);
        botonImprimir.addActionListener(e -> imprimir());
        Boton botonReiniciar = new Boton("Reiniciar", Tema.GRIS, false);
        botonReiniciar.addActionListener(e -> reiniciar());

        Tarjeta tarjeta = new Tarjeta(null);
        tarjeta.add(Componentes.fila(botonAgregar, botonImprimir, botonReiniciar,
                Componentes.etiqueta("   También puedes hacer clic en una computadora para que envíe su trabajo.",
                        Tema.TEXTO, Tema.GRIS)), BorderLayout.NORTH);
        tarjeta.add(lienzo, BorderLayout.CENTER);
        setContenido(tarjeta);
    }

    @Override
    public void cargarEjemplo() {
        reiniciar();
        for (String pc : new String[] { "PC2", "PC5", "PC3", "PC5", "PC1" }) {
            cola.encolar(pc);
        }
        impresos.add("PC4");
        ultimoEmisor = "PC1";
        mensaje("Cola de ejemplo: 5 de 6 lugares ocupados. El frente es PC2.", TipoMensaje.INFO);
        lienzo.repaint();
    }

    /**
     * Agrega un trabajo al final de la cola.
     *
     * @param pc computadora que lo envía, o null para elegirla al azar
     */
    public void agregar(String pc) {
        String emisor = pc == null ? cola.encolarAleatorio() : cola.encolar(pc);
        if (emisor == null) {
            alerta = "La cola de impresión está LLENA";
            mensaje("La cola de impresión está LLENA (" + ColaImpresion.CAPACIDAD + " de "
                    + ColaImpresion.CAPACIDAD + "). Imprime un documento para liberar lugar.", TipoMensaje.ERROR);
            lienzo.repaint();
            return;
        }
        alerta = null;
        ultimoEmisor = emisor;
        iniciarAnimacion(emisor, true);
        mensaje(emisor + " envió un trabajo; quedó al final de la cola. Cola: " + textoCola(), TipoMensaje.EXITO);
    }

    /** Atiende el trabajo del frente de la cola y lo manda a la impresora. */
    public void imprimir() {
        String atendido = cola.atender();
        if (atendido == null) {
            alerta = "La cola de impresión está VACÍA";
            mensaje("La cola de impresión está VACÍA: no hay trabajos pendientes.", TipoMensaje.ERROR);
            lienzo.repaint();
            return;
        }
        alerta = null;
        impresos.add(atendido);
        iniciarAnimacion(atendido, false);
        mensaje("Imprimiendo el documento de " + atendido + ". Los demás avanzan un lugar. Cola: " + textoCola(),
                TipoMensaje.EXITO);
    }

    private void reiniciar() {
        cola = new ColaImpresion();
        impresos.clear();
        ultimoEmisor = null;
        alerta = null;
        progreso = 1;
        mensaje("Cola reiniciada y vacía.", TipoMensaje.INFO);
        lienzo.repaint();
    }

    private String textoCola() {
        List<String> trabajos = cola.getTrabajos();
        return trabajos.isEmpty() ? "(vacía)" : String.join(" → ", trabajos);
    }

    private void iniciarAnimacion(String pc, boolean entra) {
        animacionPc = pc;
        animacionEntra = entra;
        progreso = 0;
        temporizador.restart();
    }

    private void avanzarAnimacion() {
        progreso = Math.min(1, progreso + 0.06);
        if (progreso >= 1) {
            temporizador.stop();
        }
        lienzo.repaint();
    }

    private static Color colorDe(String pc) {
        int i = Integer.parseInt(pc.substring(2)) - 1;
        return COLORES_PC[Math.max(0, Math.min(4, i))];
    }

    /** Lienzo con las computadoras, la cola y la impresora. */
    private class LienzoCola extends JComponent {
        private final List<Rectangle> areasPc = new ArrayList<>();

        LienzoCola() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (int i = 0; i < areasPc.size(); i++) {
                        if (areasPc.get(i).contains(e.getPoint())) {
                            agregar(ColaImpresion.COMPUTADORAS[i]);
                            return;
                        }
                    }
                }
            });
        }

        private Point centroPc(int i) {
            int paso = Math.max(70, (getHeight() - 150) / 5);
            return new Point(80, 40 + i * paso + 30);
        }

        private Rectangle ranura(int indiceDesdeFrente) {
            int x1 = getWidth() - 290;
            int ancho = Math.min(104, (x1 - 230) / ColaImpresion.CAPACIDAD);
            int y = getHeight() / 2 - 70;
            return new Rectangle(x1 - (indiceDesdeFrente + 1) * ancho, y, ancho - 10, 84);
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            int w = getWidth();
            int h = getHeight();
            areasPc.clear();

            // Computadoras.
            for (int i = 0; i < 5; i++) {
                String pc = ColaImpresion.COMPUTADORAS[i];
                Point c = centroPc(i);
                areasPc.add(new Rectangle(c.x - 46, c.y - 30, 92, 64));
                boolean activa = pc.equals(ultimoEmisor);
                Color color = colorDe(pc);
                g.setColor(activa ? Tema.aclarar(color, 0.85) : Tema.GRIS_SUAVE);
                g.fillRoundRect(c.x - 46, c.y - 30, 92, 64, 12, 12);
                g.setColor(Tema.TINTA);
                g.fillRoundRect(c.x - 26, c.y - 22, 52, 34, 6, 6);
                g.setColor(color);
                g.fillRoundRect(c.x - 22, c.y - 18, 44, 26, 4, 4);
                g.setColor(Tema.TINTA);
                g.fillRect(c.x - 4, c.y + 12, 8, 6);
                g.fillRoundRect(c.x - 14, c.y + 17, 28, 4, 4, 4);
                g.setColor(Color.WHITE);
                g.setFont(Tema.PEQUENA_NEGRITA);
                Tema.centrado(g, pc, c.x, c.y);
                g.setColor(Tema.GRIS);
                g.setFont(Tema.PEQUENA);
                if (activa) {
                    g.setColor(color);
                    g.drawString("último envío", c.x + 50, c.y + 4);
                }
            }

            // Cola.
            Rectangle frente = ranura(0);
            Rectangle fin = ranura(ColaImpresion.CAPACIDAD - 1);
            g.setColor(Tema.GRIS_SUAVE);
            g.fillRoundRect(fin.x - 14, frente.y - 16, frente.x + frente.width - fin.x + 28, frente.height + 32, 18, 18);
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.setColor(Tema.GRIS);
            g.drawString("FINAL  (entran aquí)", fin.x - 4, frente.y - 24);
            String textoFrente = "FRENTE  (sale primero)";
            g.drawString(textoFrente, frente.x + frente.width - g.getFontMetrics().stringWidth(textoFrente),
                    frente.y - 24);

            List<String> trabajos = cola.getTrabajos();
            int visibles = trabajos.size();
            if (animacionEntra && progreso < 1) {
                visibles--;
            }
            for (int i = 0; i < ColaImpresion.CAPACIDAD; i++) {
                Rectangle r = ranura(i);
                if (i < visibles) {
                    dibujarTrabajo(g, trabajos.get(i), r.x, r.y, r.width, r.height, i + 1);
                } else {
                    g.setColor(Tema.LINEA);
                    g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                            new float[] { 6f, 5f }, 0f));
                    g.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
                    g.setStroke(new BasicStroke(1f));
                }
            }
            g.setColor(Tema.GRIS);
            g.setStroke(new BasicStroke(2f));
            PanelInicio.flecha(g, fin.x - 60, frente.y + frente.height / 2, fin.x - 20, frente.y + frente.height / 2);
            PanelInicio.flecha(g, frente.x + frente.width + 18, frente.y + frente.height / 2,
                    frente.x + frente.width + 60, frente.y + frente.height / 2);

            // Ocupación.
            int ocupados = trabajos.size();
            int barraX = fin.x - 14;
            int barraY = frente.y + frente.height + 34;
            int barraW = frente.x + frente.width - fin.x + 28;
            g.setColor(Tema.GRIS_SUAVE);
            g.fillRoundRect(barraX, barraY, barraW, 12, 12, 12);
            g.setColor(ocupados == ColaImpresion.CAPACIDAD ? Tema.ROJO : acento);
            g.fillRoundRect(barraX, barraY, barraW * ocupados / ColaImpresion.CAPACIDAD, 12, 12, 12);
            g.setFont(Tema.TEXTO_NEGRITA);
            g.setColor(Tema.TINTA);
            g.drawString(ocupados + " / " + ColaImpresion.CAPACIDAD + " lugares ocupados", barraX, barraY + 32);
            if (alerta != null) {
                g.setFont(Tema.SUBTITULO);
                int ancho = g.getFontMetrics().stringWidth(alerta) + 36;
                g.setColor(Tema.ROJO_SUAVE);
                g.fillRoundRect(barraX + barraW - ancho, barraY + 14, ancho, 30, 30, 30);
                g.setColor(Tema.ROJO);
                g.drawString(alerta, barraX + barraW - ancho + 18, barraY + 35);
            }

            // Impresora.
            int px = w - 210;
            int py = frente.y - 10;
            g.setColor(Tema.TINTA);
            g.fillRoundRect(px, py, 170, 92, 16, 16);
            g.setColor(new Color(0x344054));
            g.fillRoundRect(px + 20, py - 18, 130, 26, 8, 8);
            g.setColor(new Color(0x12B76A));
            g.fillOval(px + 146, py + 14, 9, 9);
            g.setColor(Color.WHITE);
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.drawString("IMPRESORA", px + 16, py + 24);
            if (!impresos.isEmpty()) {
                String ultimo = impresos.get(impresos.size() - 1);
                int salida = (!animacionEntra && progreso < 1) ? (int) (60 * progreso) : 60;
                g.setColor(Color.WHITE);
                g.fillRect(px + 30, py + 78, 110, 8 + salida);
                g.setColor(Tema.LINEA);
                g.drawRect(px + 30, py + 78, 110, 8 + salida);
                g.setColor(colorDe(ultimo));
                g.setFont(Tema.TEXTO_NEGRITA);
                if (salida > 30) {
                    Tema.centrado(g, "Impresión " + ultimo, px + 85, py + 78 + salida - 6);
                }
            }

            // Animación de un trabajo en movimiento.
            if (progreso < 1 && animacionPc != null) {
                Point desde;
                Point hasta;
                if (animacionEntra) {
                    Point c = centroPc(Integer.parseInt(animacionPc.substring(2)) - 1);
                    Rectangle destino = ranura(trabajos.size() - 1);
                    desde = new Point(c.x - destino.width / 2, c.y - destino.height / 2);
                    hasta = new Point(destino.x, destino.y);
                } else {
                    desde = new Point(frente.x, frente.y);
                    hasta = new Point(px + 30, py + 10);
                }
                double t = 1 - Math.pow(1 - progreso, 3);
                int x = (int) (desde.x + (hasta.x - desde.x) * t);
                int y = (int) (desde.y + (hasta.y - desde.y) * t);
                dibujarTrabajo(g, animacionPc, x, y, frente.width, frente.height, 0);
            }

            // Historial de impresos.
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.setColor(Tema.GRIS);
            int hy = h - 40;
            g.drawString("IMPRESOS EN ORDEN DE LLEGADA", 230, hy);
            g.setFont(Tema.TEXTO);
            g.setColor(Tema.TINTA);
            g.drawString(impresos.isEmpty() ? "Todavía no se imprime nada." : String.join("  →  ", impresos),
                    230, hy + 20);
            g.dispose();
        }

        private void dibujarTrabajo(Graphics2D g, String pc, int x, int y, int ancho, int alto, int turno) {
            Color color = colorDe(pc);
            g.setColor(color);
            g.fillRoundRect(x, y, ancho, alto, 12, 12);
            g.setColor(Color.WHITE);
            g.fillRect(x + ancho / 2 - 14, y + 12, 28, 34);
            g.setColor(Tema.aclarar(color, 0.5));
            for (int i = 0; i < 4; i++) {
                g.fillRect(x + ancho / 2 - 10, y + 18 + i * 7, 20, 3);
            }
            g.setColor(Color.WHITE);
            g.setFont(Tema.TEXTO_NEGRITA);
            Tema.centrado(g, pc, x + ancho / 2, y + alto - 18);
            if (turno > 0) {
                g.setFont(Tema.PEQUENA);
                Tema.centrado(g, "turno " + turno, x + ancho / 2, y + alto - 5);
            }
        }
    }
}
