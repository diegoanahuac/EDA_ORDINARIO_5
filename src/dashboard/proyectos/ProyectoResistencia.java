package dashboard.proyectos;

import dashboard.Componentes.Boton;
import dashboard.Componentes.Tarjeta;
import dashboard.Componentes;
import dashboard.PanelProyecto;
import dashboard.Tema;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import resistencia.modelo.CircuitoParalelo;
import resistencia.modelo.CircuitoResistencias;
import resistencia.modelo.CircuitoSerie;
import resistencia.modelo.ColorResistencia;
import resistencia.modelo.Resistencia;

/**
 * Proyecto 1. Muestra en modo gráfico los colores de una resistencia a partir
 * de su valor y genera circuitos de n resistencias en serie o en paralelo.
 * Usa las clases del equipo: Resistencia, ColorResistencia (arreglo de colores)
 * y CircuitoSerie / CircuitoParalelo.
 */
public class ProyectoResistencia extends PanelProyecto {
    private static final long MINIMO = 10;
    private static final long MAXIMO = 99_000_000_000L;

    private final JTextField campoValor = Componentes.campo(12, "5800");
    private final LienzoResistor lienzoResistor = new LienzoResistor();
    private final LienzoCircuito lienzoCircuito = new LienzoCircuito();
    private final JSpinner spinnerN = new JSpinner(new SpinnerNumberModel(4, 1, 12, 1));
    private final JRadioButton radioSerie = new JRadioButton("Serie", true);
    private final JRadioButton radioParalelo = new JRadioButton("Paralelo");

    private Resistencia resistencia;
    private long valorEscrito;
    private CircuitoResistencias circuito;

    /** Construye el panel del proyecto 1. */
    public ProyectoResistencia() {
        super(1, "Resistencia eléctrica", "Arreglos",
                "Escribe un valor para ver sus bandas de color, o genera n resistencias aleatorias en serie o en paralelo.");

        // Tarjeta 1: código de colores.
        Tarjeta tarjetaColores = new Tarjeta("Código de colores");
        Boton botonVer = new Boton("Ver colores", acento, true);
        botonVer.addActionListener(e -> mostrarValor());
        campoValor.addActionListener(e -> mostrarValor());
        JPanel controles = Componentes.fila(new JLabel("Valor en ohmios:"), campoValor, botonVer,
                new JLabel("   Ejemplos:"));
        for (long ejemplo : new long[] { 160, 5800, 47_000, 2_200_000 }) {
            Boton b = new Boton(Tema.miles(ejemplo), acento, false);
            b.addActionListener(e -> {
                campoValor.setText(String.valueOf(ejemplo));
                mostrarValor();
            });
            controles.add(b);
        }
        tarjetaColores.add(controles, BorderLayout.CENTER);
        tarjetaColores.add(lienzoResistor, BorderLayout.SOUTH);

        // Tarjeta 2: circuito.
        Tarjeta tarjetaCircuito = new Tarjeta("Circuito de n resistencias aleatorias (10 Ω a 1 000 000 000 Ω)");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioSerie);
        grupo.add(radioParalelo);
        radioSerie.setOpaque(false);
        radioParalelo.setOpaque(false);
        Boton botonGenerar = new Boton("Generar circuito", acento, true);
        botonGenerar.addActionListener(e -> generarCircuito());
        radioSerie.addActionListener(e -> generarCircuito());
        radioParalelo.addActionListener(e -> generarCircuito());
        JPanel controlesCircuito = Componentes.fila(new JLabel("n ="), spinnerN, radioSerie, radioParalelo, botonGenerar);
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setOpaque(false);
        cuerpo.add(controlesCircuito, BorderLayout.NORTH);
        cuerpo.add(lienzoCircuito, BorderLayout.CENTER);
        tarjetaCircuito.add(cuerpo, BorderLayout.CENTER);

        JPanel columnas = new JPanel(new GridLayout(2, 1, 0, 14));
        columnas.setOpaque(false);
        columnas.add(tarjetaColores);
        columnas.add(tarjetaCircuito);
        setContenido(columnas);
        cargarEjemplo();
    }

    @Override
    public void cargarEjemplo() {
        campoValor.setText("5800");
        mostrarValor();
        spinnerN.setValue(5);
        radioParalelo.setSelected(true);
        generarCircuito();
    }

    /**
     * Muestra un valor y genera un circuito sin usar los controles.
     *
     * @param valor ohmios de la resistencia a dibujar
     * @param n cantidad de resistencias del circuito
     * @param serie true = serie, false = paralelo
     */
    public void mostrar(long valor, int n, boolean serie) {
        campoValor.setText(String.valueOf(valor));
        mostrarValor();
        spinnerN.setValue(n);
        (serie ? radioSerie : radioParalelo).setSelected(true);
        generarCircuito();
    }

    /** Lee el valor escrito, valida el rango y dibuja las bandas. */
    private void mostrarValor() {
        String texto = campoValor.getText().trim().replace(",", "").replace(" ", "");
        long valor;
        try {
            valor = Long.parseLong(texto);
        } catch (NumberFormatException ex) {
            mensaje("Escribe un número entero de ohmios, por ejemplo 5800.", TipoMensaje.ERROR);
            return;
        }
        if (valor < MINIMO || valor > MAXIMO) {
            mensaje("Con tres bandas el valor debe estar entre 10 Ω y 99 000 000 000 Ω.", TipoMensaje.ERROR);
            return;
        }
        valorEscrito = valor;
        resistencia = new Resistencia(valor);
        long representado = valorRepresentado(resistencia);
        if (representado != valor) {
            mensaje("Tres bandas solo guardan 2 dígitos significativos: " + Tema.miles(valor)
                    + " Ω se marca como " + Tema.miles(representado) + " Ω.", TipoMensaje.AVISO);
        } else {
            ColorResistencia[] b = resistencia.obtenerBandas();
            mensaje(Tema.miles(valor) + " Ω  →  " + b[0] + ", " + b[1] + ", " + b[2], TipoMensaje.EXITO);
        }
        lienzoResistor.repaint();
    }

    /** Genera el circuito con las clases del equipo y lo dibuja. */
    private void generarCircuito() {
        int n = (Integer) spinnerN.getValue();
        circuito = radioSerie.isSelected() ? new CircuitoSerie(n) : new CircuitoParalelo(n);
        mensaje("Circuito en " + circuito.getTipo().toLowerCase() + " con " + n + " resistencias: R total = "
                + Tema.ohmios(circuito.calcularTotal()), TipoMensaje.EXITO);
        lienzoCircuito.repaint();
    }

    private static long valorRepresentado(Resistencia r) {
        ColorResistencia[] b = r.obtenerBandas();
        return (b[0].ordinal() * 10L + b[1].ordinal()) * (long) Math.pow(10, b[2].ordinal());
    }

    /**
     * Dibuja una resistencia pequeña horizontal con sus tres bandas.
     */
    private static void resistorChico(Graphics2D g, int x, int y, int w, int h, Resistencia r) {
        g.setPaint(new GradientPaint(x, y, new Color(0xF1E0BC), x, y + h, new Color(0xD8BE8A)));
        g.fillRoundRect(x, y, w, h, h, h);
        ColorResistencia[] b = r.obtenerBandas();
        int anchoBanda = Math.max(3, w / 9);
        for (int i = 0; i < 3; i++) {
            g.setColor(b[i].getColorAwt());
            g.fillRect(x + w / 5 + i * (anchoBanda + anchoBanda / 2 + 1), y, anchoBanda, h);
        }
        g.setColor(new Color(0xC9A227));
        g.fillRect(x + w - w / 5 - anchoBanda, y, anchoBanda, h);
        g.setColor(new Color(0xA88A57));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w, h, h, h);
    }

    /** Lienzo con la resistencia grande, sus etiquetas y el arreglo de colores. */
    private class LienzoResistor extends JComponent {
        LienzoResistor() {
            setPreferredSize(new Dimension(600, 250));
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            if (resistencia == null) {
                return;
            }
            int w = getWidth();
            int cx = Math.min(w / 2 - 120, 360);
            int cy = 70;
            int largo = 300;
            int alto = 70;
            int x0 = cx - largo / 2;

            g.setColor(new Color(0x98A2B3));
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x0 - 90, cy, x0 + 10, cy);
            g.drawLine(x0 + largo - 10, cy, x0 + largo + 90, cy);
            g.setPaint(new GradientPaint(0, cy - alto / 2f, new Color(0xF6E7C8), 0, cy + alto / 2f, new Color(0xD6B97F)));
            g.fillRoundRect(x0, cy - alto / 2, largo, alto, 60, 60);
            g.setColor(new Color(0xA88A57));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(x0, cy - alto / 2, largo, alto, 60, 60);

            ColorResistencia[] bandas = resistencia.obtenerBandas();
            String[] rol = { "1ª banda · 1er dígito", "2ª banda · 2º dígito", "3ª banda · multiplicador" };
            String[] valor = { String.valueOf(bandas[0].ordinal()), String.valueOf(bandas[1].ordinal()),
                "× " + Tema.miles(Math.pow(10, bandas[2].ordinal())) };
            int[] xs = { x0 + 62, x0 + 108, x0 + 154 };
            int[] etiquetaX = { x0 - 80, x0 + 70, x0 + 220 };
            for (int i = 0; i < 3; i++) {
                g.setColor(bandas[i].getColorAwt());
                g.fillRect(xs[i], cy - alto / 2 + 1, 22, alto - 2);
                if (bandas[i] == ColorResistencia.BLANCO) {
                    g.setColor(Tema.LINEA);
                    g.drawRect(xs[i], cy - alto / 2 + 1, 22, alto - 2);
                }
                g.setColor(Tema.GRIS_CLARO);
                g.setStroke(new BasicStroke(1f));
                g.drawLine(xs[i] + 11, cy + alto / 2 + 4, etiquetaX[i] + 70, cy + alto / 2 + 26);
                g.setColor(Tema.GRIS);
                g.setFont(Tema.PEQUENA);
                g.drawString(rol[i], etiquetaX[i], cy + alto / 2 + 42);
                g.setColor(Tema.TINTA);
                g.setFont(Tema.TEXTO_NEGRITA);
                g.drawString(bandas[i] + "  =  " + valor[i], etiquetaX[i], cy + alto / 2 + 60);
            }
            g.setColor(new Color(0xC9A227));
            g.fillRect(x0 + largo - 70, cy - alto / 2 + 1, 18, alto - 2);

            // Fórmula.
            long representado = valorRepresentado(resistencia);
            g.setFont(Tema.SUBTITULO);
            g.setColor(acento);
            String formula = bandas[0].ordinal() + "" + bandas[1].ordinal() + " × 10^" + bandas[2].ordinal()
                    + "  =  " + Tema.miles(representado) + " Ω  (" + Tema.ohmios(representado) + ")";
            g.drawString(formula, x0 - 80, cy + alto / 2 + 92);
            g.setFont(Tema.PEQUENA);
            g.setColor(Tema.GRIS);
            g.drawString("La banda dorada es la tolerancia (±5 %); el proyecto pide solo las tres primeras.",
                    x0 - 80, cy + alto / 2 + 112);

            // Arreglo de colores del enum (índice = dígito = exponente).
            int ax = Math.max(x0 + largo + 130, w - 300);
            int ay = 6;
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.setColor(Tema.GRIS);
            g.drawString("ColorResistencia.values()  (arreglo)", ax, ay + 8);
            ColorResistencia[] todos = ColorResistencia.values();
            for (int i = 0; i < todos.length; i++) {
                int y = ay + 16 + i * 21;
                boolean usado = todos[i] == bandas[0] || todos[i] == bandas[1] || todos[i] == bandas[2];
                g.setColor(usado ? Tema.aclarar(acento, 0.85) : Tema.GRIS_SUAVE);
                g.fillRoundRect(ax, y, 260, 18, 6, 6);
                g.setColor(todos[i].getColorAwt());
                g.fillRoundRect(ax + 30, y + 3, 26, 12, 4, 4);
                g.setColor(Tema.LINEA);
                g.drawRoundRect(ax + 30, y + 3, 26, 12, 4, 4);
                g.setFont(Tema.MONO);
                g.setColor(Tema.GRIS);
                g.drawString("[" + i + "]", ax + 4, y + 13);
                g.setFont(usado ? Tema.PEQUENA_NEGRITA : Tema.PEQUENA);
                g.setColor(Tema.TINTA);
                g.drawString(todos[i].toString(), ax + 64, y + 13);
                g.setColor(Tema.GRIS);
                g.drawString("dígito " + i + " · ×10^" + i, ax + 150, y + 13);
            }
            g.dispose();
        }
    }

    /** Lienzo con el diagrama del circuito y el total. */
    private class LienzoCircuito extends JComponent {
        LienzoCircuito() {
            setPreferredSize(new Dimension(600, 220));
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            if (circuito == null) {
                return;
            }
            List<Resistencia> lista = new ArrayList<>(circuito.getResistencias());
            java.util.Collections.reverse(lista);
            int n = lista.size();
            int w = getWidth() - 300;
            int h = getHeight();
            boolean serie = circuito instanceof CircuitoSerie;

            g.setColor(Tema.TINTA);
            g.setStroke(new BasicStroke(2f));
            if (serie) {
                int y = h / 2 - 10;
                int x0 = 40;
                int x1 = w - 10;
                g.drawLine(x0, y, x1, y);
                terminal(g, x0, y, "A");
                terminal(g, x1, y, "B");
                int paso = (x1 - x0) / n;
                int ancho = Math.min(80, paso - 16);
                for (int i = 0; i < n; i++) {
                    int cx = x0 + paso * i + paso / 2;
                    g.setColor(Color.WHITE);
                    g.fillRect(cx - ancho / 2 - 2, y - 12, ancho + 4, 24);
                    resistorChico(g, cx - ancho / 2, y - 11, ancho, 22, lista.get(i));
                    g.setColor(Tema.GRIS);
                    g.setFont(Tema.PEQUENA_NEGRITA);
                    Tema.centrado(g, "R" + (i + 1), cx, y - 20);
                    g.setFont(Tema.PEQUENA);
                    Tema.centrado(g, Tema.ohmios(lista.get(i).getValorOhmios()), cx, y + 30);
                }
            } else {
                int x0 = 70;
                int x1 = w - 40;
                int top = 14;
                int bottom = h - 14;
                g.drawLine(x0, top, x0, bottom);
                g.drawLine(x1, top, x1, bottom);
                terminal(g, x0, (top + bottom) / 2, "A");
                terminal(g, x1, (top + bottom) / 2, "B");
                int paso = (bottom - top) / n;
                int alto = Math.min(20, paso - 6);
                int ancho = 90;
                int cx = (x0 + x1) / 2;
                for (int i = 0; i < n; i++) {
                    int y = top + paso * i + paso / 2;
                    g.setColor(Tema.TINTA);
                    g.setStroke(new BasicStroke(1.6f));
                    g.drawLine(x0, y, x1, y);
                    g.setColor(Color.WHITE);
                    g.fillRect(cx - ancho / 2 - 2, y - alto / 2 - 1, ancho + 4, alto + 2);
                    resistorChico(g, cx - ancho / 2, y - alto / 2, ancho, alto, lista.get(i));
                    etiquetaConFondo(g, "R" + (i + 1), cx - ancho / 2 - 40, y, Tema.PEQUENA_NEGRITA);
                    etiquetaConFondo(g, Tema.ohmios(lista.get(i).getValorOhmios()), cx + ancho / 2 + 8, y, Tema.PEQUENA);
                }
            }

            // Resultado.
            int rx = getWidth() - 270;
            g.setColor(Tema.aclarar(acento, 0.9));
            g.fillRoundRect(rx, 10, 260, h - 20, 14, 14);
            g.setColor(Tema.GRIS);
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.drawString(serie ? "SERIE" : "PARALELO", rx + 16, 36);
            g.setFont(Tema.MONO);
            g.drawString(serie ? "Rts = R1 + R2 + … + Rn" : "Rtp = 1 / (1/R1 + … + 1/Rn)", rx + 16, 58);
            g.setColor(acento);
            g.setFont(Tema.TITULO.deriveFont(26f));
            g.drawString(Tema.ohmios(circuito.calcularTotal()), rx + 16, 102);
            g.setFont(Tema.TEXTO);
            g.setColor(Tema.TINTA);
            g.drawString(Tema.miles(circuito.calcularTotal()) + " Ω", rx + 16, 126);
            g.setColor(Tema.GRIS);
            g.setFont(Tema.PEQUENA);
            g.drawString(n + " resistencias generadas al azar", rx + 16, 152);
            g.drawString(serie ? "Siempre es mayor que cada R." : "Siempre es menor que la R más chica.", rx + 16, 170);
            g.dispose();
        }

        private void etiquetaConFondo(Graphics2D g, String texto, int x, int y, java.awt.Font fuente) {
            g.setFont(fuente);
            int ancho = g.getFontMetrics().stringWidth(texto);
            g.setColor(Color.WHITE);
            g.fillRoundRect(x - 4, y - 9, ancho + 8, 17, 8, 8);
            g.setColor(Tema.GRIS);
            g.drawString(texto, x, y + 4);
        }

        private void terminal(Graphics2D g, int x, int y, String nombre) {
            g.setColor(Tema.TINTA);
            g.fillOval(x - 6, y - 6, 12, 12);
            g.setFont(Tema.PEQUENA_NEGRITA);
            g.drawString(nombre, "A".equals(nombre) ? x - 18 : x + 10, y + 4);
        }
    }
}
