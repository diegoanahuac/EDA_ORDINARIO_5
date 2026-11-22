package dashboard.clase;

import dashboard.Tema;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * Pestaña "Código y clase" de un proyecto: a la izquierda las funciones que
 * usa el proyecto; a la derecha el tema de clase del que salen (Brightspace,
 * Contenido, Parcial 1 o 2), cómo se relaciona y el código real del método.
 */
public class PanelCodigoClase extends JPanel {
    private static final Set<String> RESERVADAS = Set.of(
            "public", "private", "protected", "static", "final", "void", "int", "long", "double",
            "boolean", "char", "new", "return", "if", "else", "for", "while", "do", "null", "true",
            "false", "this", "class", "extends", "implements", "throw", "throws", "try", "catch",
            "break", "continue", "switch", "case", "default", "abstract", "interface", "enum", "var");
    private static final Pattern TOKEN = Pattern.compile(
            "(//.*)|(/\\*.*?\\*/)|(\"(?:\\\\.|[^\"\\\\])*\")|('(?:\\\\.|[^'\\\\])')|(\\b[A-Za-z_]\\w*\\b)|(\\b\\d+(?:\\.\\d+)?\\b)");

    private static final Color FONDO_CODIGO = new Color(0x0F172A);
    private static final Color TEXTO_CODIGO = new Color(0xE2E8F0);
    private static final Color RESERVADA = new Color(0x93C5FD);
    private static final Color COMENTARIO = new Color(0x94A3B8);
    private static final Color CADENA = new Color(0x86EFAC);
    private static final Color NUMERO = new Color(0xFCD34D);
    private static final Color TIPO = new Color(0xF0ABFC);

    private final Color acento;
    private final JLabel concepto = new JLabel();
    private final JLabel chipParcial = new JLabel();
    private final JLabel tema = new JLabel();
    private final JLabel material = new JLabel();
    private final JTextArea explicacion = new JTextArea();
    private final JTextPane codigo = new JTextPane();
    private final JTextArea numeros = new JTextArea();
    private final JLabel ubicacion = new JLabel();

    /**
     * @param numero número del proyecto (1 a 6)
     * @param acento color del proyecto
     */
    public PanelCodigoClase(int numero, Color acento) {
        super(new BorderLayout(14, 0));
        this.acento = acento;
        setOpaque(false);

        List<Referencia> referencias = CatalogoClase.de(numero);
        DefaultListModel<Referencia> modelo = new DefaultListModel<>();
        referencias.forEach(modelo::addElement);
        JList<Referencia> lista = new JList<>(modelo);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setCellRenderer(new Renderizador());
        lista.setBackground(Color.WHITE);
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && lista.getSelectedValue() != null) {
                mostrar(lista.getSelectedValue());
            }
        });
        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setBorder(BorderFactory.createLineBorder(Tema.LINEA));
        scrollLista.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLista.setPreferredSize(new Dimension(290, 100));

        JPanel izquierda = new JPanel(new BorderLayout(0, 8));
        izquierda.setOpaque(false);
        JLabel tituloLista = new JLabel("Funciones del proyecto");
        tituloLista.setFont(Tema.SUBTITULO);
        tituloLista.setForeground(Tema.TINTA);
        izquierda.add(tituloLista, BorderLayout.NORTH);
        izquierda.add(scrollLista, BorderLayout.CENTER);
        JLabel nota = new JLabel("<html>El código se lee del archivo real en <b>src/</b>.</html>");
        nota.setFont(Tema.PEQUENA);
        nota.setForeground(Tema.GRIS);
        izquierda.add(nota, BorderLayout.SOUTH);
        add(izquierda, BorderLayout.WEST);

        add(detalle(), BorderLayout.CENTER);

        if (!referencias.isEmpty()) {
            lista.setSelectedIndex(0);
        }
    }

    private JComponent detalle() {
        JPanel derecha = new JPanel(new BorderLayout(0, 10));
        derecha.setOpaque(false);

        concepto.setFont(Tema.SUBTITULO);
        concepto.setForeground(Tema.TINTA);
        chipParcial.setFont(Tema.PEQUENA_NEGRITA);
        chipParcial.setForeground(acento);
        chipParcial.setOpaque(true);
        chipParcial.setBackground(Tema.aclarar(acento, 0.85));
        chipParcial.setBorder(BorderFactory.createEmptyBorder(3, 9, 3, 9));
        JPanel filaTitulo = new JPanel(new BorderLayout(10, 0));
        filaTitulo.setOpaque(false);
        filaTitulo.add(concepto, BorderLayout.CENTER);
        filaTitulo.add(chipParcial, BorderLayout.EAST);

        JPanel clase = new Caja(Tema.AZUL_SUAVE);
        clase.setLayout(new BorderLayout(0, 4));
        JLabel visto = new JLabel("VISTO EN CLASE · Brightspace › Contenido");
        visto.setFont(Tema.PEQUENA_NEGRITA);
        visto.setForeground(Tema.AZUL);
        tema.setFont(Tema.TEXTO_NEGRITA);
        tema.setForeground(Tema.TINTA);
        material.setFont(Tema.PEQUENA);
        material.setForeground(Tema.GRIS);
        JPanel textosClase = new JPanel(new BorderLayout(0, 2));
        textosClase.setOpaque(false);
        textosClase.add(tema, BorderLayout.NORTH);
        textosClase.add(material, BorderLayout.SOUTH);
        clase.add(visto, BorderLayout.NORTH);
        clase.add(textosClase, BorderLayout.CENTER);

        explicacion.setFont(Tema.TEXTO);
        explicacion.setForeground(Tema.TINTA);
        explicacion.setLineWrap(true);
        explicacion.setWrapStyleWord(true);
        explicacion.setEditable(false);
        explicacion.setOpaque(false);
        JPanel relacion = new Caja(Color.WHITE);
        relacion.setLayout(new BorderLayout(0, 4));
        JLabel titRelacion = new JLabel("CÓMO SE RELACIONA CON LO VISTO EN CLASE");
        titRelacion.setFont(Tema.PEQUENA_NEGRITA);
        titRelacion.setForeground(Tema.GRIS);
        relacion.add(titRelacion, BorderLayout.NORTH);
        relacion.add(explicacion, BorderLayout.CENTER);

        JPanel arriba = new JPanel(new BorderLayout(0, 10));
        arriba.setOpaque(false);
        arriba.add(filaTitulo, BorderLayout.NORTH);
        JPanel cajas = new JPanel(new BorderLayout(0, 10));
        cajas.setOpaque(false);
        cajas.add(clase, BorderLayout.NORTH);
        cajas.add(relacion, BorderLayout.CENTER);
        arriba.add(cajas, BorderLayout.CENTER);
        derecha.add(arriba, BorderLayout.NORTH);

        codigo.setEditable(false);
        codigo.setBackground(FONDO_CODIGO);
        codigo.setFont(Tema.MONO.deriveFont(13f));
        codigo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        numeros.setEditable(false);
        numeros.setBackground(new Color(0x111827));
        numeros.setForeground(new Color(0x64748B));
        numeros.setFont(Tema.MONO.deriveFont(13f));
        numeros.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        JPanel sinAjuste = new JPanel(new BorderLayout());
        sinAjuste.add(codigo, BorderLayout.CENTER);
        JScrollPane scrollCodigo = new JScrollPane(sinAjuste);
        scrollCodigo.setRowHeaderView(numeros);
        scrollCodigo.setBorder(BorderFactory.createLineBorder(new Color(0x1E293B)));
        scrollCodigo.getVerticalScrollBar().setUnitIncrement(16);

        JPanel panelCodigo = new JPanel(new BorderLayout(0, 6));
        panelCodigo.setOpaque(false);
        JLabel titCodigo = new JLabel("CÓDIGO DEL PROYECTO (raw)");
        titCodigo.setFont(Tema.PEQUENA_NEGRITA);
        titCodigo.setForeground(Tema.GRIS);
        ubicacion.setFont(Tema.MONO.deriveFont(11f));
        ubicacion.setForeground(Tema.GRIS);
        JPanel filaCodigo = new JPanel(new BorderLayout());
        filaCodigo.setOpaque(false);
        filaCodigo.add(titCodigo, BorderLayout.WEST);
        filaCodigo.add(ubicacion, BorderLayout.EAST);
        panelCodigo.add(filaCodigo, BorderLayout.NORTH);
        panelCodigo.add(scrollCodigo, BorderLayout.CENTER);
        derecha.add(panelCodigo, BorderLayout.CENTER);
        return derecha;
    }

    private void mostrar(Referencia r) {
        concepto.setText(r.getConcepto());
        chipParcial.setText(r.getParcial());
        tema.setText(r.getTema());
        material.setText("Material: " + r.getMaterial());
        explicacion.setText(r.getExplicacion());

        ExtractorCodigo.Fragmento f = ExtractorCodigo.extraer(r.getArchivo(), r.getFirma());
        pintarCodigo(f.getCodigo());
        StringBuilder nums = new StringBuilder();
        int total = f.getCodigo().split("\n", -1).length;
        for (int i = 0; i < total; i++) {
            nums.append(f.isEncontrado() ? f.getLineaInicial() + i : i + 1);
            if (i < total - 1) {
                nums.append('\n');
            }
        }
        numeros.setText(nums.toString());
        ubicacion.setText(f.isEncontrado()
                ? "src/" + r.getArchivo() + " · líneas " + f.getLineaInicial() + "–" + f.getLineaFinal()
                : "src/" + r.getArchivo());
        codigo.setCaretPosition(0);
    }

    /** Colorea el código: palabras reservadas, tipos, cadenas, números y comentarios. */
    private void pintarCodigo(String texto) {
        StyledDocument doc = codigo.getStyledDocument();
        codigo.setText(texto);
        SimpleAttributeSet base = estilo(TEXTO_CODIGO, false);
        doc.setCharacterAttributes(0, texto.length(), base, true);
        Matcher m = TOKEN.matcher(texto);
        while (m.find()) {
            SimpleAttributeSet estilo = null;
            if (m.group(1) != null || m.group(2) != null) {
                estilo = estilo(COMENTARIO, false);
            } else if (m.group(3) != null || m.group(4) != null) {
                estilo = estilo(CADENA, false);
            } else if (m.group(5) != null) {
                String palabra = m.group(5);
                if (RESERVADAS.contains(palabra)) {
                    estilo = estilo(RESERVADA, true);
                } else if (Character.isUpperCase(palabra.charAt(0))) {
                    estilo = estilo(TIPO, false);
                }
            } else if (m.group(6) != null) {
                estilo = estilo(NUMERO, false);
            }
            if (estilo != null) {
                doc.setCharacterAttributes(m.start(), m.end() - m.start(), estilo, false);
            }
        }
    }

    private static SimpleAttributeSet estilo(Color color, boolean negrita) {
        SimpleAttributeSet s = new SimpleAttributeSet();
        StyleConstants.setForeground(s, color);
        StyleConstants.setBold(s, negrita);
        return s;
    }

    /** Caja con esquinas redondeadas. */
    private static class Caja extends JPanel {
        private final Color fondo;

        Caja(Color fondo) {
            this.fondo = fondo;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Tema.suavizar(g2);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(Tema.LINEA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.dispose();
        }
    }

    /** Celda de la lista: concepto y, debajo, parcial y tema. */
    private class Renderizador extends JPanel implements ListCellRenderer<Referencia> {
        private final JLabel arriba = new JLabel();
        private final JLabel abajo = new JLabel();

        Renderizador() {
            super(new BorderLayout(0, 2));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Tema.GRIS_SUAVE),
                    BorderFactory.createEmptyBorder(9, 12, 9, 12)));
            arriba.setFont(Tema.TEXTO_NEGRITA);
            abajo.setFont(Tema.PEQUENA);
            add(arriba, BorderLayout.NORTH);
            add(abajo, BorderLayout.SOUTH);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Referencia> list, Referencia valor,
                                                      int indice, boolean seleccionado, boolean foco) {
            // Ancho fijo para que los nombres largos bajen de línea en vez de cortarse.
            arriba.setText("<html><div style='width:230px'>" + valor.getConcepto() + "</div></html>");
            abajo.setText(valor.getTema());
            setBackground(seleccionado ? Tema.aclarar(acento, 0.85) : Color.WHITE);
            arriba.setForeground(seleccionado ? acento.darker() : Tema.TINTA);
            abajo.setForeground(Tema.GRIS);
            return this;
        }
    }
}
