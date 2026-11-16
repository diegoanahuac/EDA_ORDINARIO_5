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
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import rutamagica.Arista;
import rutamagica.Dijkstra;
import rutamagica.Grafo;

/**
 * Proyecto 6. Grafo ponderado no dirigido de los Pueblos Mágicos de Yucatán.
 * Se elige origen y destino en el mapa y se calcula la ruta más corta con las
 * clases Grafo y Dijkstra del equipo; también se puede ver Dijkstra paso a paso.
 */
public class ProyectoRutaMagica extends PanelProyecto {
    private static final String ARCHIVO = "datos/pueblos_yucatan.txt";
    private static final List<String> DATOS_INTERNOS = Arrays.asList(
            "Izamal Motul 37.5", "Izamal Valladolid 110", "Izamal Espita 80.5", "Izamal Mani 136",
            "Motul Sisal 100", "Motul Espita 124", "Valladolid Espita 51.2", "Valladolid Tekax 164",
            "Sisal Mani 159", "Mani Tekax 26.8");
    /** Nombre completo y coordenadas aproximadas (latitud, longitud) de cada pueblo. */
    private static final Map<String, Object[]> PUEBLOS = new LinkedHashMap<>();

    static {
        PUEBLOS.put("Sisal", new Object[] { "Sisal", 21.166, -90.030 });
        PUEBLOS.put("Motul", new Object[] { "Motul de Carrillo Puerto", 21.096, -89.284 });
        PUEBLOS.put("Izamal", new Object[] { "Izamal", 20.931, -89.018 });
        PUEBLOS.put("Espita", new Object[] { "Espita", 21.012, -88.305 });
        PUEBLOS.put("Valladolid", new Object[] { "Valladolid", 20.690, -88.202 });
        PUEBLOS.put("Mani", new Object[] { "Maní", 20.387, -89.392 });
        PUEBLOS.put("Tekax", new Object[] { "Tekax de Álvaro Obregón", 20.205, -89.287 });
    }

    private final Grafo grafo;
    private final boolean desdeArchivo;
    private final JComboBox<String> comboOrigen = new JComboBox<>();
    private final JComboBox<String> comboDestino = new JComboBox<>();
    private final JLabel etiquetaRuta = new JLabel();
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[] { "Pueblo", "Distancia", "Estado" }, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final LienzoMapa lienzo = new LienzoMapa();
    private final Timer temporizador;

    private String origen;
    private String destino;
    private Dijkstra dijkstra;
    private List<String> ruta = new ArrayList<>();
    private int pasoVisible = -1;

    /** Construye el panel del proyecto 6. */
    public ProyectoRutaMagica() {
        super(6, "Ruta mágica · Yucatán", "Grafo ponderado · Dijkstra",
                "Haz clic en un pueblo para el origen y en otro para el destino. Las aristas son carreteras en km.");
        Grafo cargado;
        boolean archivo;
        Path archivoGrafo = Paths.get(ARCHIVO);
        try {
            archivo = Files.exists(archivoGrafo);
            cargado = archivo ? Grafo.desdeArchivo(ARCHIVO) : Grafo.desdeLineas(DATOS_INTERNOS);
        } catch (IOException ex) {
            cargado = Grafo.desdeLineas(DATOS_INTERNOS);
            archivo = false;
        }
        grafo = cargado;
        desdeArchivo = archivo;
        temporizador = new Timer(900, e -> avanzarPaso());

        for (String p : grafo.getPueblos()) {
            comboOrigen.addItem(nombre(p));
            comboDestino.addItem(nombre(p));
        }
        Boton botonCalcular = new Boton("Calcular ruta", acento, true);
        botonCalcular.addActionListener(e -> calcularDesdeCombos(false));
        Boton botonPasos = new Boton("Dijkstra paso a paso", acento, false);
        botonPasos.addActionListener(e -> calcularDesdeCombos(true));
        Boton botonLimpiar = new Boton("Limpiar", Tema.GRIS, false);
        botonLimpiar.addActionListener(e -> limpiar());

        Tarjeta lateral = new Tarjeta("Consulta");
        lateral.setPreferredSize(new Dimension(360, 400));
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        columna.add(Componentes.fila(new JLabel("Origen "), comboOrigen));
        columna.add(Componentes.fila(new JLabel("Destino"), comboDestino));
        columna.add(Componentes.fila(botonCalcular, botonPasos));
        columna.add(Componentes.fila(botonLimpiar, Componentes.etiqueta(
                desdeArchivo ? "Grafo leído de " + ARCHIVO : "Grafo con datos internos", Tema.PEQUENA, Tema.GRIS)));
        etiquetaRuta.setFont(Tema.TEXTO);
        etiquetaRuta.setAlignmentX(0f);
        etiquetaRuta.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));
        columna.add(etiquetaRuta);

        JTable tabla = new JTable(modeloTabla);
        tabla.setFont(Tema.TEXTO);
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(Tema.TEXTO_NEGRITA);
        tabla.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.LINEA));
        scroll.setPreferredSize(new Dimension(320, 200));
        lateral.add(columna, BorderLayout.CENTER);
        lateral.add(scroll, BorderLayout.SOUTH);

        Tarjeta tarjetaMapa = new Tarjeta("Pueblos Mágicos de Yucatán (7 vértices, 10 aristas, lista de adyacencia)");
        tarjetaMapa.add(lienzo, BorderLayout.CENTER);

        JPanel cuerpo = new JPanel(new BorderLayout(14, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(tarjetaMapa, BorderLayout.CENTER);
        cuerpo.add(lateral, BorderLayout.EAST);
        setContenido(cuerpo);
        limpiar();
    }

    @Override
    public void cargarEjemplo() {
        consultar("Sisal", "Espita", false);
    }

    private static String nombre(String clave) {
        Object[] datos = PUEBLOS.get(clave);
        return datos == null ? clave : (String) datos[0];
    }

    private String clave(String nombreCompleto) {
        for (String p : grafo.getPueblos()) {
            if (nombre(p).equals(nombreCompleto)) {
                return p;
            }
        }
        return nombreCompleto;
    }

    private void calcularDesdeCombos(boolean pasoAPaso) {
        consultar(clave((String) comboOrigen.getSelectedItem()), clave((String) comboDestino.getSelectedItem()),
                pasoAPaso);
    }

    /**
     * Calcula la ruta más corta entre dos pueblos.
     *
     * @param desde clave del pueblo de origen
     * @param hasta clave del pueblo de destino
     * @param pasoAPaso true para animar cada paso de Dijkstra
     */
    public void consultar(String desde, String hasta, boolean pasoAPaso) {
        temporizador.stop();
        origen = desde;
        destino = hasta;
        comboOrigen.setSelectedItem(nombre(desde));
        comboDestino.setSelectedItem(nombre(hasta));
        dijkstra = new Dijkstra(grafo, origen);
        if (pasoAPaso) {
            ruta = new ArrayList<>();
            pasoVisible = 0;
            etiquetaRuta.setText("<html><b>Dijkstra paso a paso</b><br>Sale de la cola el pueblo más cercano"
                    + " y se relajan sus carreteras.</html>");
            actualizarTabla();
            mensaje("Paso 1: sale " + nombre(dijkstra.getPasos().get(0).getPueblo()) + " (distancia 0).",
                    TipoMensaje.INFO);
            temporizador.start();
        } else {
            pasoVisible = dijkstra.getPasos().size() - 1;
            mostrarResultado();
        }
        lienzo.repaint();
    }

    private void avanzarPaso() {
        List<Dijkstra.Paso> pasos = dijkstra.getPasos();
        if (pasoVisible >= pasos.size() - 1) {
            temporizador.stop();
            mostrarResultado();
            return;
        }
        pasoVisible++;
        Dijkstra.Paso paso = pasos.get(pasoVisible);
        actualizarTabla();
        mensaje("Paso " + (pasoVisible + 1) + ": sale " + nombre(paso.getPueblo()) + " con "
                + Tema.miles(paso.getDistancias().get(paso.getPueblo())) + " km; se revisan sus vecinos.",
                TipoMensaje.INFO);
        lienzo.repaint();
    }

    private void mostrarResultado() {
        ruta = dijkstra.getRuta(destino);
        actualizarTabla();
        if (origen.equals(destino)) {
            etiquetaRuta.setText("<html><b>Origen y destino son el mismo pueblo.</b><br>Distancia total: 0 km</html>");
            mensaje("Origen y destino son el mismo pueblo: 0 km.", TipoMensaje.AVISO);
        } else if (ruta.isEmpty()) {
            etiquetaRuta.setText("<html><b style='color:#B42318'>NO EXISTE RUTA</b></html>");
            mensaje("No existe ruta entre " + nombre(origen) + " y " + nombre(destino) + ".", TipoMensaje.ERROR);
        } else {
            StringBuilder html = new StringBuilder("<html><b>RUTA ENCONTRADA</b><br><br>");
            for (int i = 0; i < ruta.size() - 1; i++) {
                html.append(nombre(ruta.get(i))).append(" → ").append(nombre(ruta.get(i + 1)))
                        .append(" &nbsp;<b>").append(Tema.miles(peso(ruta.get(i), ruta.get(i + 1)))).append(" km</b><br>");
            }
            html.append("<br><span style='font-size:15px;color:#1E3A8A'><b>Distancia total: ")
                    .append(Tema.miles(dijkstra.getDistancia(destino))).append(" km</b></span></html>");
            etiquetaRuta.setText(html.toString());
            mensaje("Ruta más corta " + nombre(origen) + " → " + nombre(destino) + ": "
                    + String.join(" → ", ruta) + " = " + Tema.miles(dijkstra.getDistancia(destino)) + " km.",
                    TipoMensaje.EXITO);
        }
        lienzo.repaint();
    }

    private void limpiar() {
        temporizador.stop();
        origen = null;
        destino = null;
        dijkstra = null;
        ruta = new ArrayList<>();
        pasoVisible = -1;
        etiquetaRuta.setText("<html>Elige origen y destino en el mapa<br>o en las listas.</html>");
        actualizarTabla();
        mensaje("Haz clic en un pueblo para elegir el origen.", TipoMensaje.INFO);
        lienzo.repaint();
    }

    private double peso(String a, String b) {
        double menor = Double.POSITIVE_INFINITY;
        for (Arista arista : grafo.getVecinos(a)) {
            if (arista.getDestino().equals(b)) {
                menor = Math.min(menor, arista.getDistancia());
            }
        }
        return menor;
    }

    private Set<String> extraidos() {
        Set<String> conjunto = new HashSet<>();
        if (dijkstra != null) {
            for (int i = 0; i <= pasoVisible && i < dijkstra.getPasos().size(); i++) {
                conjunto.add(dijkstra.getPasos().get(i).getPueblo());
            }
        }
        return conjunto;
    }

    private Map<String, Double> distanciasVisibles() {
        if (dijkstra == null || pasoVisible < 0) {
            return new HashMap<>();
        }
        return dijkstra.getPasos().get(Math.min(pasoVisible, dijkstra.getPasos().size() - 1)).getDistancias();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        Map<String, Double> distancias = distanciasVisibles();
        Set<String> listos = extraidos();
        for (String p : grafo.getPueblos()) {
            Double d = distancias.get(p);
            String texto = d == null ? "—" : d.isInfinite() ? "∞" : Tema.miles(d) + " km";
            String estado = dijkstra == null ? "" : listos.contains(p) ? "definitiva"
                    : d != null && !d.isInfinite() ? "tentativa" : "sin visitar";
            modeloTabla.addRow(new Object[] { nombre(p), texto, estado });
        }
    }

    /** Mapa esquemático del estado con pueblos, carreteras y la ruta. */
    private class LienzoMapa extends JComponent {
        private final Map<String, Point> posiciones = new HashMap<>();

        LienzoMapa() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (Map.Entry<String, Point> entrada : posiciones.entrySet()) {
                        if (entrada.getValue().distance(e.getPoint()) < 22) {
                            elegir(entrada.getKey());
                            return;
                        }
                    }
                }
            });
        }

        private void elegir(String pueblo) {
            if (origen == null || destino != null) {
                temporizador.stop();
                origen = pueblo;
                destino = null;
                dijkstra = null;
                ruta = new ArrayList<>();
                pasoVisible = -1;
                comboOrigen.setSelectedItem(nombre(pueblo));
                actualizarTabla();
                etiquetaRuta.setText("<html>Origen: <b>" + nombre(pueblo) + "</b><br>Ahora elige el destino.</html>");
                mensaje("Origen: " + nombre(pueblo) + ". Haz clic en el pueblo de destino.", TipoMensaje.INFO);
                repaint();
            } else {
                consultar(origen, pueblo, false);
            }
        }

        private void calcularPosiciones() {
            posiciones.clear();
            double minLat = 90, maxLat = -90, minLon = 180, maxLon = -180;
            for (Object[] d : PUEBLOS.values()) {
                minLat = Math.min(minLat, (Double) d[1]);
                maxLat = Math.max(maxLat, (Double) d[1]);
                minLon = Math.min(minLon, (Double) d[2]);
                maxLon = Math.max(maxLon, (Double) d[2]);
            }
            int margenX = 110;
            int margenY = 80;
            int w = getWidth() - 2 * margenX;
            int h = getHeight() - 2 * margenY;
            int indice = 0;
            for (String p : grafo.getPueblos()) {
                Object[] d = PUEBLOS.get(p);
                if (d == null) {
                    // Pueblo sin coordenadas: se coloca en un círculo.
                    double a = 2 * Math.PI * indice / grafo.getPueblos().size();
                    posiciones.put(p, new Point((int) (getWidth() / 2 + Math.cos(a) * w / 3),
                            (int) (getHeight() / 2 + Math.sin(a) * h / 3)));
                } else {
                    int x = margenX + (int) (((Double) d[2] - minLon) / (maxLon - minLon) * w);
                    int y = margenY + (int) ((maxLat - (Double) d[1]) / (maxLat - minLat) * h);
                    posiciones.put(p, new Point(x, y));
                }
                indice++;
            }
        }

        private boolean enRuta(String a, String b) {
            for (int i = 0; i < ruta.size() - 1; i++) {
                if ((ruta.get(i).equals(a) && ruta.get(i + 1).equals(b))
                        || (ruta.get(i).equals(b) && ruta.get(i + 1).equals(a))) {
                    return true;
                }
            }
            return false;
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            calcularPosiciones();
            int w = getWidth();
            int h = getHeight();

            // Fondo: golfo arriba y península abajo.
            g.setColor(new Color(0xE0F2FE));
            g.fillRoundRect(0, 0, w, h, 14, 14);
            g.setColor(new Color(0xF0FDF4));
            g.fillRoundRect(16, 52, w - 32, h - 68, 60, 60);
            g.setColor(new Color(0xBAE6FD));
            g.setFont(Tema.SUBTITULO);
            g.drawString("Golfo de México", 24, 32);
            g.setColor(new Color(0xBBF7D0));
            g.setFont(Tema.TITULO.deriveFont(54f));
            g.drawString("YUCATÁN", w - 330, h - 34);

            // Aristas.
            List<String> pueblos = grafo.getPueblos();
            for (int i = 0; i < pueblos.size(); i++) {
                for (Arista a : grafo.getVecinos(pueblos.get(i))) {
                    int j = pueblos.indexOf(a.getDestino());
                    if (j <= i) {
                        continue;
                    }
                    Point p1 = posiciones.get(pueblos.get(i));
                    Point p2 = posiciones.get(a.getDestino());
                    boolean resaltada = enRuta(pueblos.get(i), a.getDestino());
                    g.setColor(resaltada ? Tema.VERDE : new Color(0x94A3B8));
                    g.setStroke(new BasicStroke(resaltada ? 6f : 2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    String km = Tema.miles(a.getDistancia()) + " km";
                    g.setFont(Tema.PEQUENA_NEGRITA);
                    int ancho = g.getFontMetrics().stringWidth(km) + 12;
                    int mx = (p1.x + p2.x) / 2;
                    int my = (p1.y + p2.y) / 2;
                    g.setColor(resaltada ? Tema.VERDE : Color.WHITE);
                    g.fillRoundRect(mx - ancho / 2, my - 10, ancho, 20, 20, 20);
                    g.setColor(resaltada ? Color.WHITE : Tema.TINTA);
                    Tema.centrado(g, km, mx, my + 4);
                }
            }

            // Pueblos.
            Set<String> listos = extraidos();
            Map<String, Double> distancias = distanciasVisibles();
            String actual = (dijkstra != null && pasoVisible >= 0 && temporizador.isRunning())
                    ? dijkstra.getPasos().get(pasoVisible).getPueblo() : null;
            for (String p : pueblos) {
                Point c = posiciones.get(p);
                boolean esOrigen = p.equals(origen);
                boolean esDestino = p.equals(destino);
                Color relleno = esOrigen ? Tema.AZUL_MEDIO : esDestino ? Tema.VERDE
                        : ruta.contains(p) ? new Color(0x86EFAC) : listos.contains(p) ? new Color(0xFDE68A) : Color.WHITE;
                if (p.equals(actual)) {
                    g.setColor(new Color(0xF59E0B));
                    g.fillOval(c.x - 24, c.y - 24, 48, 48);
                }
                g.setColor(relleno);
                g.fillOval(c.x - 15, c.y - 15, 30, 30);
                g.setColor(Tema.TINTA);
                g.setStroke(new BasicStroke(2.2f));
                g.drawOval(c.x - 15, c.y - 15, 30, 30);
                g.setFont(Tema.PEQUENA_NEGRITA);
                g.setColor(esOrigen || esDestino ? Color.WHITE : Tema.TINTA);
                Tema.centrado(g, esOrigen ? "A" : esDestino ? "B" : "", c.x, c.y + 4);

                g.setFont(Tema.TEXTO_NEGRITA);
                g.setColor(Tema.TINTA);
                String nombreVisible = nombre(p);
                int ancho = g.getFontMetrics().stringWidth(nombreVisible);
                int ty = c.y - 22;
                g.setColor(new Color(255, 255, 255, 210));
                g.fillRoundRect(c.x - ancho / 2 - 6, ty - 14, ancho + 12, 19, 8, 8);
                g.setColor(Tema.TINTA);
                Tema.centrado(g, nombreVisible, c.x, ty);
                Double d = distancias.get(p);
                if (d != null) {
                    g.setFont(Tema.PEQUENA_NEGRITA);
                    g.setColor(d.isInfinite() ? Tema.GRIS : Tema.AZUL);
                    Tema.centrado(g, "d = " + (d.isInfinite() ? "∞" : Tema.miles(d)), c.x, c.y + 32);
                }
            }

            // Leyenda.
            g.setFont(Tema.PEQUENA);
            int lx = 24;
            int ly = h - 70;
            Object[][] leyenda = { { Tema.AZUL_MEDIO, "A = origen" }, { Tema.VERDE, "B = destino / ruta" },
                { new Color(0xFDE68A), "distancia definitiva" }, { Color.WHITE, "sin procesar" } };
            for (int i = 0; i < leyenda.length; i++) {
                g.setColor((Color) leyenda[i][0]);
                g.fillOval(lx, ly + i * 16, 11, 11);
                g.setColor(Tema.TINTA);
                g.drawOval(lx, ly + i * 16, 11, 11);
                g.drawString((String) leyenda[i][1], lx + 18, ly + i * 16 + 10);
            }
            g.dispose();
        }
    }
}
