package dashboard;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Colores, tipografías y utilidades de dibujo compartidas por todo el dashboard.
 */
public final class Tema {
    public static final Color FONDO = new Color(0xF5F7FA);
    public static final Color TINTA = new Color(0x172033);
    public static final Color GRIS = new Color(0x667085);
    public static final Color GRIS_CLARO = new Color(0x98A2B3);
    public static final Color LINEA = new Color(0xD0D5DD);
    public static final Color GRIS_SUAVE = new Color(0xF2F4F7);
    public static final Color AZUL = new Color(0x1E3A8A);
    public static final Color AZUL_MEDIO = new Color(0x2563EB);
    public static final Color AZUL_SUAVE = new Color(0xEFF6FF);
    public static final Color VERDE = new Color(0x15803D);
    public static final Color VERDE_SUAVE = new Color(0xECFDF3);
    public static final Color ROJO = new Color(0xB42318);
    public static final Color ROJO_SUAVE = new Color(0xFEF3F2);
    public static final Color AMBAR = new Color(0xB54708);
    public static final Color AMBAR_SUAVE = new Color(0xFFFAEB);
    public static final Color LATERAL = new Color(0x0F172A);
    public static final Color LATERAL_ACTIVO = new Color(0x1E293B);

    /** Color de acento de cada proyecto, en orden del 1 al 6. */
    public static final Color[] ACENTOS = {
        new Color(0xB54708), // 1 Resistencia
        new Color(0x6941C6), // 2 Carrito
        new Color(0x0E7490), // 3 Navegador
        new Color(0xC11574), // 4 Cola
        new Color(0x15803D), // 5 Sistema de archivos
        new Color(0x1E3A8A)  // 6 Ruta mágica
    };

    public static final Font TITULO = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SUBTITULO = new Font("SansSerif", Font.BOLD, 15);
    public static final Font TEXTO = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font TEXTO_NEGRITA = new Font("SansSerif", Font.BOLD, 13);
    public static final Font PEQUENA = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font PEQUENA_NEGRITA = new Font("SansSerif", Font.BOLD, 11);
    public static final Font MONO = new Font("Monospaced", Font.PLAIN, 12);

    private static final DecimalFormat MILES =
            new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat PESOS =
            new DecimalFormat("$#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));

    private Tema() {
    }

    /**
     * Activa el suavizado de líneas y texto.
     *
     * @param g contexto gráfico
     */
    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Mezcla un color con blanco.
     *
     * @param color color base
     * @param cantidad 0 = color original, 1 = blanco
     * @return el color aclarado
     */
    public static Color aclarar(Color color, double cantidad) {
        int r = (int) (color.getRed() + (255 - color.getRed()) * cantidad);
        int g = (int) (color.getGreen() + (255 - color.getGreen()) * cantidad);
        int b = (int) (color.getBlue() + (255 - color.getBlue()) * cantidad);
        return new Color(r, g, b);
    }

    /** @return el número con separador de miles, por ejemplo 5,800 */
    public static String miles(double valor) {
        return MILES.format(valor);
    }

    /** @return la cantidad en pesos, por ejemplo $1,234.50 */
    public static String pesos(double valor) {
        return PESOS.format(valor);
    }

    /**
     * Da formato a una resistencia con su prefijo: Ω, kΩ, MΩ o GΩ.
     *
     * @param ohmios valor en ohmios
     * @return texto corto, por ejemplo 4.7 kΩ
     */
    public static String ohmios(double ohmios) {
        if (ohmios >= 1e9) {
            return MILES.format(ohmios / 1e9) + " GΩ";
        }
        if (ohmios >= 1e6) {
            return MILES.format(ohmios / 1e6) + " MΩ";
        }
        if (ohmios >= 1e3) {
            return MILES.format(ohmios / 1e3) + " kΩ";
        }
        return MILES.format(ohmios) + " Ω";
    }

    /**
     * Da formato a un tamaño en bytes: B, KB, MB o GB.
     *
     * @param bytes tamaño en bytes
     * @return texto corto, por ejemplo 2.5 MB
     */
    public static String bytes(long bytes) {
        if (bytes >= 1_000_000_000L) {
            return MILES.format(bytes / 1e9) + " GB";
        }
        if (bytes >= 1_000_000L) {
            return MILES.format(bytes / 1e6) + " MB";
        }
        if (bytes >= 1_000L) {
            return MILES.format(bytes / 1e3) + " KB";
        }
        return bytes + " B";
    }

    /**
     * Recorta un texto para que quepa en un ancho.
     *
     * @param g contexto con la fuente ya puesta
     * @param texto texto original
     * @param ancho ancho máximo en píxeles
     * @return el texto, con "…" si se recortó
     */
    public static String recortar(Graphics2D g, String texto, int ancho) {
        if (g.getFontMetrics().stringWidth(texto) <= ancho) {
            return texto;
        }
        String t = texto;
        while (t.length() > 1 && g.getFontMetrics().stringWidth(t + "…") > ancho) {
            t = t.substring(0, t.length() - 1);
        }
        return t + "…";
    }

    /**
     * Dibuja un texto centrado horizontalmente en x.
     *
     * @param g contexto gráfico
     * @param texto texto
     * @param x centro horizontal
     * @param y línea base
     */
    public static void centrado(Graphics2D g, String texto, int x, int y) {
        g.drawString(texto, x - g.getFontMetrics().stringWidth(texto) / 2, y);
    }
}
