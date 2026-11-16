package dashboard.proyectos;

import archivos.ArbolArchivos;
import archivos.Nodo;
import dashboard.Componentes;
import dashboard.Componentes.Boton;
import dashboard.Componentes.Tarjeta;
import dashboard.PanelProyecto;
import dashboard.Tema;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * Proyecto 5. Sistema de archivos sobre un árbol binario de búsqueda
 * (ArbolArchivos y Nodo del equipo). Se dibuja el árbol, se puede insertar,
 * buscar (resaltando el camino), eliminar (tres casos del ABB) y animar los
 * recorridos inorden, preorden y postorden.
 */
public class ProyectoArchivos extends PanelProyecto {
    private ArbolArchivos arbol;
    private final LienzoArbol lienzo = new LienzoArbol();
    private final JRadioButton radioDirectorio = new JRadioButton("Directorio", true);
    private final JRadioButton radioArchivo = new JRadioButton("Archivo");
    private final JTextField campoNombre = Componentes.campo(12, "");
    private final JTextField campoExtension = Componentes.campo(5, "");
    private final JTextField campoTamano = Componentes.campo(8, "");
    private final JTextField campoBuscar = Componentes.campo(12, "");
    private final JLabel etiquetaInfo = new JLabel();
    private final JTextArea areaRecorrido = new JTextArea(3, 40);
    private final Timer temporizador;

    private Nodo seleccionado;
    private List<Nodo> camino = new ArrayList<>();
    private final Map<Nodo, Integer> visitados = new LinkedHashMap<>();
    private List<Nodo> recorrido = new ArrayList<>();
    private String nombreRecorrido = "";
    private int pasoRecorrido;

    /** Construye el panel del proyecto 5. */
    public ProyectoArchivos() {
        super(5, "Sistema de archivos", "Árbol binario de búsqueda",
                "Cada nodo es un directorio o un archivo; se ordenan por nombre. Los archivos siempre son hojas.");
        temporizador = new Timer(550, e -> pasoDeRecorrido());

        // Columna de controles.
        Tarjeta controles = new Tarjeta(null);
        JPanel columna = new JPanel();
        columna.setOpaque(false);
        columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioDirectorio);
        grupo.add(radioArchivo);
        radioDirectorio.setOpaque(false);
        radioArchivo.setOpaque(false);
        radioDirectorio.addActionListener(e -> actualizarCampos());
        radioArchivo.addActionListener(e -> actualizarCampos());

        Boton botonInsertar = new Boton("Insertar", acento, true);
        botonInsertar.addActionListener(e -> insertar());
        Boton botonBuscar = new Boton("Buscar", acento, false);
        botonBuscar.addActionListener(e -> buscar(campoBuscar.getText()));
        campoBuscar.addActionListener(e -> buscar(campoBuscar.getText()));
        Boton botonEliminar = new Boton("Eliminar", Tema.ROJO, false);
        botonEliminar.addActionListener(e -> eliminar(campoBuscar.getText(), true));
        Boton botonIn = new Boton("Inorden", acento, false);
        Boton botonPre = new Boton("Preorden", acento, false);
        Boton botonPost = new Boton("Postorden", acento, false);
        for (Boton b : new Boton[] { botonIn, botonPre, botonPost }) {
            b.setBorder(BorderFactory.createEmptyBorder(8, 9, 8, 9));
        }
        botonIn.addActionListener(e -> iniciarRecorrido("Inorden", true));
        botonPre.addActionListener(e -> iniciarRecorrido("Preorden", true));
        botonPost.addActionListener(e -> iniciarRecorrido("Postorden", true));
        Boton botonEjemplo = new Boton("Árbol de ejemplo", Tema.GRIS, false);
        botonEjemplo.addActionListener(e -> cargarEjemplo());

        columna.add(seccion("INSERTAR"));
        columna.add(Componentes.fila(radioDirectorio, radioArchivo));
        columna.add(Componentes.fila(new JLabel("Nombre"), campoNombre));
        columna.add(Componentes.fila(new JLabel("Extensión"), campoExtension));
        columna.add(Componentes.fila(new JLabel("Tamaño (bytes)"), campoTamano));
        columna.add(Componentes.fila(botonInsertar));
        columna.add(seccion("BUSCAR O ELIMINAR"));
        columna.add(Componentes.fila(new JLabel("Nombre"), campoBuscar));
        columna.add(Componentes.fila(botonBuscar, botonEliminar));
        columna.add(seccion("RECORRIDOS"));
        columna.add(Componentes.fila(botonIn, botonPre));
        columna.add(Componentes.fila(botonPost));
        columna.add(Componentes.fila(botonEjemplo));
        controles.add(columna, BorderLayout.NORTH);

        // Información y recorrido.
        Tarjeta info = new Tarjeta("Elemento seleccionado");
        etiquetaInfo.setFont(Tema.TEXTO);
        etiquetaInfo.setVerticalAlignment(JLabel.TOP);
        info.add(etiquetaInfo, BorderLayout.CENTER);

        JPanel izquierda = new JPanel(new BorderLayout(0, 12));
        izquierda.setOpaque(false);
        izquierda.setPreferredSize(new Dimension(262, 400));
        izquierda.add(controles, BorderLayout.NORTH);
        izquierda.add(info, BorderLayout.CENTER);

        areaRecorrido.setFont(Tema.TEXTO);
        areaRecorrido.setEditable(false);
        areaRecorrido.setLineWrap(true);
        areaRecorrido.setWrapStyleWord(true);
        areaRecorrido.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        areaRecorrido.setBackground(Tema.VERDE_SUAVE);
        areaRecorrido.setText("Elige un recorrido para ver el orden de visita.");
        JScrollPane scroll = new JScrollPane(areaRecorrido);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.LINEA));

        Tarjeta tarjetaArbol = new Tarjeta("Árbol (haz clic en un nodo)");
        tarjetaArbol.add(lienzo, BorderLayout.CENTER);
        tarjetaArbol.add(scroll, BorderLayout.SOUTH);

        JPanel cuerpo = new JPanel(new BorderLayout(14, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(izquierda, BorderLayout.WEST);
        cuerpo.add(tarjetaArbol, BorderLayout.CENTER);
        setContenido(cuerpo);
        actualizarCampos();
        cargarEjemplo();
    }

    private static JLabel seccion(String texto) {
        JLabel etiqueta = Componentes.etiqueta(texto, Tema.PEQUENA_NEGRITA, Tema.GRIS);
        etiqueta.setBorder(BorderFactory.createEmptyBorder(12, 8, 2, 0));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    private void actualizarCampos() {
        boolean archivo = radioArchivo.isSelected();
        campoExtension.setEnabled(archivo);
        campoTamano.setEnabled(archivo);
        if (!archivo) {
            campoExtension.setText("");
            campoTamano.setText("");
        }
    }

    @Override
    public void cargarEjemplo() {
        temporizador.stop();
        arbol = new ArbolArchivos("Documentos");
        String[][] ejemplo = {
            { "Apuntes", "", "0" }, { "Musica", "", "0" }, { "Fotos", "", "0" }, { "Videos", "", "0" },
            { "Ciencias", "", "0" }, { "Escuela", "", "0" }, { "Algebra", "pdf", "120400" },
            { "Calculo", "pdf", "98200" }, { "Gatos", "png", "2500000" }, { "Trailer", "mp4", "48000000" },
            { "Zelda", "mp3", "5200000" }
        };
        for (String[] e : ejemplo) {
            arbol.insertar(e[0], e[1], Long.parseLong(e[2]), e[1].isEmpty());
        }
        seleccionado = arbol.getRaiz();
        camino = new ArrayList<>();
        visitados.clear();
        areaRecorrido.setText("Elige un recorrido para ver el orden de visita.");
        actualizarInfo();
        lienzo.repaint();
        mensaje("Árbol de ejemplo cargado: raíz Documentos con 11 elementos.", TipoMensaje.INFO);
    }

    private void insertar() {
        boolean directorio = radioDirectorio.isSelected();
        long tamano = 0;
        if (!directorio) {
            try {
                tamano = Long.parseLong(campoTamano.getText().trim());
            } catch (NumberFormatException ex) {
                mensaje("ERROR: el tamaño debe ser un número entero de bytes.", TipoMensaje.ERROR);
                return;
            }
        }
        try {
            arbol.insertar(campoNombre.getText(), campoExtension.getText(), tamano, directorio);
        } catch (IllegalArgumentException ex) {
            camino = arbol.caminoBusqueda(campoNombre.getText());
            lienzo.repaint();
            mensaje("ERROR: " + ex.getMessage() + ".", TipoMensaje.ERROR);
            return;
        }
        visitados.clear();
        camino = arbol.caminoBusqueda(campoNombre.getText());
        seleccionado = arbol.buscar(campoNombre.getText());
        actualizarInfo();
        lienzo.repaint();
        mensaje((directorio ? "Directorio" : "Archivo") + " \"" + nombreCompleto(seleccionado)
                + "\" insertado. Camino desde la raíz: " + textoCamino(), TipoMensaje.EXITO);
        campoNombre.setText("");
    }

    /**
     * Busca un elemento y resalta el camino que siguió la búsqueda.
     *
     * @param nombre nombre a buscar
     */
    public void buscar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            mensaje("ERROR: el nombre no puede estar vacío.", TipoMensaje.ERROR);
            return;
        }
        visitados.clear();
        camino = arbol.caminoBusqueda(nombre);
        Nodo encontrado = arbol.buscar(nombre);
        if (encontrado == null) {
            mensaje("El elemento \"" + nombre.trim() + "\" no existe en el sistema de archivos. Se revisó: "
                    + textoCamino(), TipoMensaje.ERROR);
        } else {
            seleccionado = encontrado;
            actualizarInfo();
            mensaje("Elemento encontrado en " + camino.size() + " comparaciones: " + textoCamino(), TipoMensaje.EXITO);
        }
        lienzo.repaint();
    }

    /**
     * Elimina un elemento explicando qué caso del ABB se aplica.
     *
     * @param nombre nombre del elemento (si está vacío se usa el seleccionado)
     * @param confirmar true para pedir confirmación al usuario
     */
    public void eliminar(String nombre, boolean confirmar) {
        if ((nombre == null || nombre.trim().isEmpty()) && seleccionado != null) {
            nombre = seleccionado.getNombre();
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            mensaje("ERROR: escribe un nombre o selecciona un nodo.", TipoMensaje.ERROR);
            return;
        }
        Nodo nodo = arbol.buscar(nombre);
        if (nodo == null) {
            mensaje("El elemento \"" + nombre.trim() + "\" no existe en el sistema de archivos.", TipoMensaje.ERROR);
            return;
        }
        String caso = casoEliminacion(nodo);
        if (confirmar) {
            int r = JOptionPane.showConfirmDialog(this, "¿Eliminar \"" + nombreCompleto(nodo) + "\"?\n" + caso,
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (r != JOptionPane.YES_OPTION) {
                mensaje("Eliminación cancelada.", TipoMensaje.INFO);
                return;
            }
        }
        try {
            arbol.eliminar(nombre);
        } catch (IllegalArgumentException ex) {
            mensaje("ERROR: " + ex.getMessage() + ".", TipoMensaje.ERROR);
            return;
        }
        visitados.clear();
        camino = new ArrayList<>();
        seleccionado = arbol.getRaiz();
        actualizarInfo();
        lienzo.repaint();
        mensaje("\"" + nombreCompleto(nodo) + "\" eliminado. " + caso, TipoMensaje.EXITO);
    }

    private String casoEliminacion(Nodo nodo) {
        if (nodo == arbol.getRaiz()) {
            return "La raíz no se puede eliminar.";
        }
        boolean izq = nodo.getIzquierdo() != null;
        boolean der = nodo.getDerecho() != null;
        if (!izq && !der) {
            return "Caso 1: es una hoja y se quita directamente.";
        }
        if (izq != der) {
            Nodo hijo = izq ? nodo.getIzquierdo() : nodo.getDerecho();
            return "Caso 2: tiene un hijo; " + nombreCompleto(hijo) + " ocupa su lugar.";
        }
        Nodo sucesor = nodo.getDerecho();
        while (sucesor.getIzquierdo() != null) {
            sucesor = sucesor.getIzquierdo();
        }
        Nodo predecesor = nodo.getIzquierdo();
        while (predecesor.getDerecho() != null) {
            predecesor = predecesor.getDerecho();
        }
        if (sucesor.isDirectorio()) {
            return "Caso 3: tiene dos hijos; lo reemplaza su sucesor inorden " + sucesor.getNombre() + ".";
        }
        if (predecesor.isDirectorio()) {
            return "Caso 3: tiene dos hijos; el sucesor (" + nombreCompleto(sucesor)
                    + ") es archivo, así que lo reemplaza el predecesor " + predecesor.getNombre() + ".";
        }
        return "Caso 3: tiene dos hijos, pero sucesor y predecesor son archivos y un archivo no puede tener hijos.";
    }

    /**
     * Inicia un recorrido.
     *
     * @param tipo "Inorden", "Preorden" o "Postorden"
     * @param animado true para ir nodo por nodo; false para mostrarlo completo
     */
    public void iniciarRecorrido(String tipo, boolean animado) {
        temporizador.stop();
        nombreRecorrido = tipo;
        recorrido = "Inorden".equals(tipo) ? arbol.inorden()
                : "Preorden".equals(tipo) ? arbol.preorden() : arbol.postorden();
        visitados.clear();
        camino = new ArrayList<>();
        pasoRecorrido = 0;
        areaRecorrido.setText(tipo.toUpperCase() + ":  ");
        if (animado) {
            temporizador.start();
            mensaje("Recorrido " + tipo.toLowerCase() + " en curso…", TipoMensaje.INFO);
        } else {
            while (pasoRecorrido < recorrido.size()) {
                pasoDeRecorrido();
            }
        }
    }

    private void pasoDeRecorrido() {
        if (pasoRecorrido >= recorrido.size()) {
            temporizador.stop();
            return;
        }
        Nodo n = recorrido.get(pasoRecorrido);
        visitados.put(n, pasoRecorrido + 1);
        areaRecorrido.append((pasoRecorrido > 0 ? "  →  " : "") + (pasoRecorrido + 1) + ". " + nombreCompleto(n));
        pasoRecorrido++;
        if (pasoRecorrido == recorrido.size()) {
            temporizador.stop();
            String explicacion = "Inorden".equals(nombreRecorrido) ? "izquierdo, nodo, derecho: sale en orden alfabético"
                    : "Preorden".equals(nombreRecorrido) ? "nodo, izquierdo, derecho: la raíz va primero"
                    : "izquierdo, derecho, nodo: la raíz va al final";
            mensaje("Recorrido " + nombreRecorrido.toLowerCase() + " completo (" + explicacion + ").", TipoMensaje.EXITO);
        }
        lienzo.repaint();
    }

    private static String nombreCompleto(Nodo n) {
        return n.isDirectorio() ? n.getNombre() : n.getNombre() + "." + n.getExtension();
    }

    private String textoCamino() {
        List<String> nombres = new ArrayList<>();
        for (Nodo n : camino) {
            nombres.add(n.getNombre());
        }
        return String.join(" → ", nombres);
    }

    private void actualizarInfo() {
        if (seleccionado == null) {
            etiquetaInfo.setText("");
            return;
        }
        Nodo n = seleccionado;
        String hijoIzq = n.getIzquierdo() == null ? "—" : nombreCompleto(n.getIzquierdo());
        String hijoDer = n.getDerecho() == null ? "—" : nombreCompleto(n.getDerecho());
        etiquetaInfo.setText("<html><div style='width:200px'>"
                + "<b style='font-size:14px'>" + nombreCompleto(n) + "</b><br>"
                + "Tipo: <b>" + (n.isDirectorio() ? "Directorio" : "Archivo") + "</b><br>"
                + "Extensión: " + (n.isDirectorio() ? "—" : n.getExtension()) + "<br>"
                + "Tamaño: " + Tema.miles(n.getTamanio()) + " bytes<br>"
                + "Hijo izquierdo: " + hijoIzq + "<br>"
                + "Hijo derecho: " + hijoDer + "<br>"
                + "<span style='color:#667085'>" + casoEliminacion(n) + "</span>"
                + "<br>Altura del árbol: " + arbol.altura() + "</div></html>");
    }

    /** Lienzo que dibuja el árbol con una columna por posición inorden. */
    private class LienzoArbol extends JComponent {
        private final Map<Nodo, Rectangle> areas = new HashMap<>();

        LienzoArbol() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (Map.Entry<Nodo, Rectangle> entrada : areas.entrySet()) {
                        if (entrada.getValue().contains(e.getPoint())) {
                            seleccionado = entrada.getKey();
                            campoBuscar.setText(seleccionado.getNombre());
                            actualizarInfo();
                            repaint();
                            mensaje("Seleccionado: " + nombreCompleto(seleccionado) + ". "
                                    + casoEliminacion(seleccionado), TipoMensaje.INFO);
                            return;
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics gr) {
            Graphics2D g = (Graphics2D) gr.create();
            Tema.suavizar(g);
            areas.clear();
            List<Nodo> orden = arbol.inorden();
            Map<Nodo, Integer> columna = new HashMap<>();
            for (int i = 0; i < orden.size(); i++) {
                columna.put(orden.get(i), i);
            }
            Map<Nodo, Integer> nivel = new HashMap<>();
            calcularNiveles(arbol.getRaiz(), 0, nivel);
            double paso = (getWidth() - 20) / (double) Math.max(1, orden.size());
            int ancho = (int) Math.min(118, paso - 6);
            int alto = 60;
            int separacionY = Math.min(96, (getHeight() - 70) / Math.max(1, arbol.altura()));
            Map<Nodo, int[]> centros = new HashMap<>();
            for (Nodo n : orden) {
                int cx = (int) (10 + paso * columna.get(n) + paso / 2);
                int cy = 34 + nivel.get(n) * separacionY;
                centros.put(n, new int[] { cx, cy });
            }
            // Aristas.
            for (Nodo n : orden) {
                int[] c = centros.get(n);
                for (Nodo hijo : new Nodo[] { n.getIzquierdo(), n.getDerecho() }) {
                    if (hijo != null) {
                        int[] h = centros.get(hijo);
                        boolean enCamino = camino.contains(n) && camino.contains(hijo);
                        g.setColor(enCamino ? Tema.AZUL_MEDIO : Tema.LINEA);
                        g.setStroke(new BasicStroke(enCamino ? 3.5f : 2f));
                        g.drawLine(c[0], c[1] + alto / 2, h[0], h[1] - alto / 2);
                        g.setFont(Tema.PEQUENA);
                        g.setColor(Tema.GRIS_CLARO);
                        g.drawString(hijo == n.getIzquierdo() ? "izq" : "der",
                                (c[0] + h[0]) / 2 + (hijo == n.getIzquierdo() ? -24 : 6), (c[1] + h[1]) / 2 + 6);
                    }
                }
            }
            // Nodos.
            for (Nodo n : orden) {
                int[] c = centros.get(n);
                Rectangle r = new Rectangle(c[0] - ancho / 2, c[1] - alto / 2, ancho, alto);
                areas.put(n, r);
                dibujarNodo(g, n, r);
            }
            g.dispose();
        }

        private void calcularNiveles(Nodo n, int nivelActual, Map<Nodo, Integer> nivel) {
            if (n == null) {
                return;
            }
            nivel.put(n, nivelActual);
            calcularNiveles(n.getIzquierdo(), nivelActual + 1, nivel);
            calcularNiveles(n.getDerecho(), nivelActual + 1, nivel);
        }

        private void dibujarNodo(Graphics2D g, Nodo n, Rectangle r) {
            Integer orden = visitados.get(n);
            boolean actual = orden != null && orden == visitados.size() && temporizador.isRunning();
            Color fondo = orden != null ? (actual ? new Color(0xD1FADF) : Tema.VERDE_SUAVE)
                    : n.isDirectorio() ? new Color(0xFFF6DB) : Color.WHITE;
            g.setColor(fondo);
            g.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);
            Color borde = n == seleccionado ? acento : camino.contains(n) ? Tema.AZUL_MEDIO
                    : orden != null ? Tema.VERDE : Tema.LINEA;
            g.setColor(borde);
            g.setStroke(new BasicStroke(n == seleccionado || camino.contains(n) || actual ? 3f : 1.4f));
            g.drawRoundRect(r.x, r.y, r.width, r.height, 10, 10);

            // Icono arriba al centro.
            int ix = r.x + r.width / 2 - 11;
            int iy = r.y + 6;
            if (n.isDirectorio()) {
                g.setColor(new Color(0xF4B740));
                g.fillRoundRect(ix, iy + 3, 22, 15, 4, 4);
                g.fillRoundRect(ix, iy, 10, 6, 3, 3);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(ix + 3, iy, 16, 19);
                g.setColor(Tema.GRIS);
                g.setStroke(new BasicStroke(1f));
                g.drawRect(ix + 3, iy, 16, 19);
                g.setColor(acento);
                g.setFont(Tema.PEQUENA_NEGRITA.deriveFont(7.5f));
                Tema.centrado(g, n.getExtension().toUpperCase(), ix + 11, iy + 13);
            }
            g.setFont(Tema.PEQUENA_NEGRITA.deriveFont(11.5f));
            g.setColor(Tema.TINTA);
            Tema.centrado(g, Tema.recortar(g, nombreCompleto(n), r.width - 6), r.x + r.width / 2, r.y + 40);
            g.setFont(Tema.PEQUENA.deriveFont(10f));
            g.setColor(Tema.GRIS);
            String detalle = n == arbol.getRaiz() ? "raíz" : n.isDirectorio() ? "directorio"
                    : Tema.bytes(n.getTamanio());
            Tema.centrado(g, Tema.recortar(g, detalle, r.width - 6), r.x + r.width / 2, r.y + 54);
            if (orden != null) {
                g.setColor(Tema.VERDE);
                g.fillOval(r.x + r.width - 14, r.y - 10, 22, 22);
                g.setColor(Color.WHITE);
                g.setFont(Tema.PEQUENA_NEGRITA);
                Tema.centrado(g, String.valueOf(orden), r.x + r.width - 3, r.y + 5);
            }
        }
    }
}
