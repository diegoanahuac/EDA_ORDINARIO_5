package dashboard.proyectos;

import carrito.Carrito;
import carrito.ItemCarrito;
import carrito.ListaProductos;
import carrito.Producto;
import dashboard.Componentes;
import dashboard.Componentes.Boton;
import dashboard.Componentes.Tarjeta;
import dashboard.PanelInicio;
import dashboard.PanelProyecto;
import dashboard.Tema;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Proyecto 2. Catálogo y carrito de compras guardados en listas enlazadas
 * (ListaProductos y Carrito del equipo). El carrito se dibuja nodo por nodo
 * con sus apuntadores; se puede agregar, modificar, eliminar y vaciar.
 */
public class ProyectoCarrito extends PanelProyecto {
    private final ListaProductos catalogo = new ListaProductos();
    private Carrito carrito = new Carrito();

    private final DefaultTableModel modeloTabla = new DefaultTableModel(new Object[] { "ID", "Producto", "Precio" }, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField campoBuscar = Componentes.campo(8, "");
    private final JSpinner spinnerCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JSpinner spinnerNueva = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
    private final JTextField campoEliminar = Componentes.campo(10, "");
    private final JLabel etiquetaTotales = Componentes.etiqueta("", Tema.SUBTITULO, Tema.TINTA);
    private final LienzoLista lienzo = new LienzoLista();
    private int seleccionado = -1;

    /** Construye el panel del proyecto 2. */
    public ProyectoCarrito() {
        super(2, "Carrito de compras", "Listas enlazadas",
                "Elige productos del catálogo; cada artículo del carrito es un nodo que apunta al siguiente.");
        crearCatalogo();

        // Catálogo.
        Tarjeta tarjetaCatalogo = new Tarjeta("Catálogo (ListaProductos)");
        tarjetaCatalogo.setPreferredSize(new Dimension(360, 400));
        tabla.setRowHeight(28);
        tabla.setFont(Tema.TEXTO);
        tabla.getTableHeader().setFont(Tema.TEXTO_NEGRITA);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setSelectionBackground(Tema.aclarar(acento, 0.8));
        tabla.setSelectionForeground(Tema.TINTA);
        tabla.getColumnModel().getColumn(0).setMaxWidth(44);
        tabla.setRowSelectionInterval(0, 0);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.LINEA));

        Boton botonBuscar = new Boton("Buscar", acento, false);
        botonBuscar.addActionListener(e -> buscarEnCatalogo());
        campoBuscar.addActionListener(e -> buscarEnCatalogo());
        Boton botonAgregar = new Boton("Agregar al carrito", acento, true);
        botonAgregar.addActionListener(e -> agregar());

        JPanel abajo = new JPanel();
        abajo.setOpaque(false);
        abajo.setLayout(new BoxLayout(abajo, BoxLayout.Y_AXIS));
        abajo.add(Componentes.fila(new JLabel("ID o nombre:"), campoBuscar, botonBuscar));
        abajo.add(Componentes.fila(new JLabel("Cantidad:"), spinnerCantidad, botonAgregar));
        tarjetaCatalogo.add(scroll, BorderLayout.CENTER);
        tarjetaCatalogo.add(abajo, BorderLayout.SOUTH);

        // Carrito.
        Tarjeta tarjetaCarrito = new Tarjeta(null);
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        cabecera.add(Componentes.etiqueta("Carrito (lista enlazada de NodoCarrito)", Tema.SUBTITULO, Tema.TINTA),
                BorderLayout.WEST);
        cabecera.add(etiquetaTotales, BorderLayout.EAST);

        Boton botonActualizar = new Boton("Cambiar cantidad", acento, false);
        botonActualizar.addActionListener(e -> actualizarCantidad());
        Boton botonQuitar = new Boton("Eliminar seleccionado", Tema.ROJO, false);
        botonQuitar.addActionListener(e -> eliminarSeleccionado());
        Boton botonEliminarTexto = new Boton("Eliminar por ID o nombre", Tema.ROJO, false);
        botonEliminarTexto.addActionListener(e -> eliminarPorTexto());
        Boton botonVaciar = new Boton("Vaciar carrito", Tema.GRIS, false);
        botonVaciar.addActionListener(e -> vaciar());
        Boton botonFinalizar = new Boton("Finalizar compra", Tema.VERDE, true);
        botonFinalizar.addActionListener(e -> finalizar());

        JPanel acciones = new JPanel();
        acciones.setOpaque(false);
        acciones.setLayout(new BoxLayout(acciones, BoxLayout.Y_AXIS));
        acciones.add(Componentes.fila(new JLabel("Nodo seleccionado → nueva cantidad:"), spinnerNueva,
                botonActualizar, botonQuitar));
        acciones.add(Componentes.fila(new JLabel("ID o nombre:"), campoEliminar, botonEliminarTexto, botonVaciar,
                botonFinalizar));

        tarjetaCarrito.add(cabecera, BorderLayout.NORTH);
        tarjetaCarrito.add(lienzo, BorderLayout.CENTER);
        tarjetaCarrito.add(acciones, BorderLayout.SOUTH);

        JPanel cuerpo = new JPanel(new BorderLayout(14, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(tarjetaCatalogo, BorderLayout.WEST);
        cuerpo.add(tarjetaCarrito, BorderLayout.CENTER);
        setContenido(cuerpo);
        cargarEjemplo();
    }

    /** Llena el catálogo (lista enlazada) con los productos del proyecto. */
    private void crearCatalogo() {
        catalogo.agregarProducto(new Producto(1, "Leche", 28.50));
        catalogo.agregarProducto(new Producto(2, "Pan", 42.00));
        catalogo.agregarProducto(new Producto(3, "Huevos", 56.00));
        catalogo.agregarProducto(new Producto(4, "Arroz", 24.50));
        catalogo.agregarProducto(new Producto(5, "Frijoles", 32.00));
        catalogo.agregarProducto(new Producto(6, "Café", 89.90));
        catalogo.agregarProducto(new Producto(7, "Queso", 74.00));
        catalogo.agregarProducto(new Producto(8, "Tortillas", 22.00));
        for (Producto p : catalogo.obtenerProductos()) {
            modeloTabla.addRow(new Object[] { p.getId(), p.getNombre(), Tema.pesos(p.getPrecio()) });
        }
    }

    @Override
    public void cargarEjemplo() {
        carrito = new Carrito();
        carrito.agregarProducto(catalogo.buscarPorId(1), 2);
        carrito.agregarProducto(catalogo.buscarPorId(3), 1);
        carrito.agregarProducto(catalogo.buscarPorId(6), 1);
        carrito.agregarProducto(catalogo.buscarPorId(2), 3);
        carrito.agregarProducto(catalogo.buscarPorId(8), 4);
        seleccionado = 2;
        refrescar();
        mensaje("Carrito de ejemplo con 5 nodos. Haz clic en un nodo para seleccionarlo.", TipoMensaje.INFO);
    }

    private Producto productoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        return catalogo.buscarPorId((Integer) modeloTabla.getValueAt(fila, 0));
    }

    /** Busca en el catálogo por ID (si es número) o por nombre. */
    private void buscarEnCatalogo() {
        String texto = campoBuscar.getText().trim();
        if (texto.isEmpty()) {
            mensaje("Escribe un ID o un nombre para buscar.", TipoMensaje.ERROR);
            return;
        }
        Producto p;
        try {
            p = catalogo.buscarPorId(Integer.parseInt(texto));
        } catch (NumberFormatException ex) {
            p = catalogo.buscarPorNombre(texto);
        }
        if (p == null) {
            mensaje("No existe \"" + texto + "\" en el catálogo.", TipoMensaje.ERROR);
            return;
        }
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((Integer) modeloTabla.getValueAt(i, 0) == p.getId()) {
                tabla.setRowSelectionInterval(i, i);
                tabla.scrollRectToVisible(tabla.getCellRect(i, 0, true));
            }
        }
        mensaje("Encontrado: " + p, TipoMensaje.EXITO);
    }

    private void agregar() {
        Producto p = productoSeleccionado();
        if (p == null) {
            mensaje("Selecciona un producto del catálogo.", TipoMensaje.ERROR);
            return;
        }
        int cantidad = (Integer) spinnerCantidad.getValue();
        boolean yaEstaba = carrito.buscarPorId(p.getId()) != null;
        carrito.agregarProducto(p, cantidad);
        seleccionado = indiceDe(p.getId());
        refrescar();
        mensaje(yaEstaba ? p.getNombre() + " ya estaba en el carrito: se sumaron " + cantidad + " unidades al mismo nodo."
                : "Nuevo nodo al final de la lista: " + cantidad + " × " + p.getNombre() + ".", TipoMensaje.EXITO);
    }

    private void actualizarCantidad() {
        ItemCarrito item = itemSeleccionado();
        if (item == null) {
            mensaje("Primero haz clic en un nodo del carrito.", TipoMensaje.ERROR);
            return;
        }
        int nueva = (Integer) spinnerNueva.getValue();
        carrito.actualizarCantidad(item.getProducto().getId(), nueva);
        refrescar();
        mensaje("Cantidad de " + item.getProducto().getNombre() + " cambiada a " + nueva
                + ". Subtotal: " + Tema.pesos(item.getSubtotal()), TipoMensaje.EXITO);
    }

    private void eliminarSeleccionado() {
        ItemCarrito item = itemSeleccionado();
        if (item == null) {
            mensaje("Primero haz clic en un nodo del carrito.", TipoMensaje.ERROR);
            return;
        }
        carrito.eliminarPorId(item.getProducto().getId());
        seleccionado = -1;
        refrescar();
        mensaje("Nodo de " + item.getProducto().getNombre() + " eliminado: su anterior ahora apunta a su siguiente.",
                TipoMensaje.EXITO);
    }

    private void eliminarPorTexto() {
        String texto = campoEliminar.getText().trim();
        if (texto.isEmpty()) {
            mensaje("Escribe el ID o el nombre del producto a eliminar.", TipoMensaje.ERROR);
            return;
        }
        boolean eliminado;
        try {
            eliminado = carrito.eliminarPorId(Integer.parseInt(texto));
        } catch (NumberFormatException ex) {
            eliminado = carrito.eliminarPorNombre(texto);
        }
        seleccionado = -1;
        refrescar();
        if (eliminado) {
            mensaje("Se eliminó \"" + texto + "\" del carrito.", TipoMensaje.EXITO);
        } else {
            mensaje("\"" + texto + "\" no está en el carrito.", TipoMensaje.ERROR);
        }
    }

    private void vaciar() {
        carrito.vaciar();
        seleccionado = -1;
        refrescar();
        mensaje("Carrito vacío: cabeza = null. La compra se reinició.", TipoMensaje.INFO);
    }

    private void finalizar() {
        List<ItemCarrito> items = carrito.obtenerItems();
        if (items.isEmpty()) {
            mensaje("No hay productos en el carrito.", TipoMensaje.AVISO);
            return;
        }
        StringBuilder ticket = new StringBuilder("TICKET DE COMPRA\n\n");
        for (ItemCarrito it : items) {
            ticket.append(String.format("%-10s %3d × %9s = %10s%n", it.getProducto().getNombre(),
                    it.getCantidad(), Tema.pesos(it.getProducto().getPrecio()), Tema.pesos(it.getSubtotal())));
        }
        ticket.append(String.format("%nArtículos: %d%nTotal a pagar: %s", carrito.contarArticulos(),
                Tema.pesos(carrito.calcularTotal())));
        JTextArea area = new JTextArea(ticket.toString());
        area.setFont(Tema.MONO);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, area, "Compra finalizada", JOptionPane.INFORMATION_MESSAGE);
        String total = Tema.pesos(carrito.calcularTotal());
        vaciar();
        mensaje("Compra finalizada por " + total + ". El carrito quedó vacío para una nueva compra.", TipoMensaje.EXITO);
    }

    private ItemCarrito itemSeleccionado() {
        List<ItemCarrito> items = carrito.obtenerItems();
        return seleccionado >= 0 && seleccionado < items.size() ? items.get(seleccionado) : null;
    }

    private int indiceDe(int id) {
        List<ItemCarrito> items = carrito.obtenerItems();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getProducto().getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void refrescar() {
        ItemCarrito item = itemSeleccionado();
        if (item != null) {
            spinnerNueva.setValue(item.getCantidad());
        }
        etiquetaTotales.setText("Artículos: " + carrito.contarArticulos() + "     Total: "
                + Tema.pesos(carrito.calcularTotal()));
        lienzo.repaint();
    }

    /** Lienzo que dibuja la lista enlazada del carrito. */
    private class LienzoLista extends JComponent {
        private static final int ANCHO = 160;
        private static final int ALTO = 82;
        private final List<Rectangle> areas = new ArrayList<>();

        LienzoLista() {
            setPreferredSize(new Dimension(600, 360));
            setBorder(BorderFactory.createEmptyBorder());
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    for (int i = 0; i < areas.size(); i++) {
                        if (areas.get(i).contains(e.getPoint())) {
                            seleccionado = i;
                            refrescar();
                            ItemCarrito it = itemSeleccionado();
                            mensaje("Nodo " + (i + 1) + " seleccionado: " + it.getProducto().getNombre()
                                    + " × " + it.getCantidad(), TipoMensaje.INFO);
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
            List<ItemCarrito> items = carrito.obtenerItems();
            g.setColor(Tema.GRIS_SUAVE);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

            int margenX = 88;
            int separacion = 38;
            int porFila = Math.max(1, (getWidth() - margenX - 30) / (ANCHO + separacion));
            int y0 = 28;

            g.setFont(Tema.TEXTO_NEGRITA);
            g.setColor(acento);
            g.drawString("cabeza", 16, y0 + ALTO / 2 + 5);
            g.setStroke(new BasicStroke(2f));
            if (items.isEmpty()) {
                PanelInicio.flecha(g, 68, y0 + ALTO / 2, 108, y0 + ALTO / 2);
                g.setColor(Tema.GRIS);
                g.drawString("null", 116, y0 + ALTO / 2 + 5);
                g.setFont(Tema.TEXTO);
                g.drawString("El carrito está vacío. Agrega productos desde el catálogo.", 16, y0 + ALTO + 40);
                g.dispose();
                return;
            }
            PanelInicio.flecha(g, 68, y0 + ALTO / 2, margenX - 2, y0 + ALTO / 2);

            for (int i = 0; i < items.size(); i++) {
                int fila = i / porFila;
                int col = i % porFila;
                int x = margenX + col * (ANCHO + separacion);
                int y = y0 + fila * (ALTO + 50);
                areas.add(new Rectangle(x, y, ANCHO, ALTO));
                dibujarNodo(g, items.get(i), x, y, i == seleccionado, i + 1);

                // Apuntador al siguiente.
                g.setColor(Tema.TINTA);
                g.setStroke(new BasicStroke(2f));
                int sx = x + ANCHO - 11;
                int sy = y + ALTO / 2;
                if (i == items.size() - 1) {
                    PanelInicio.flecha(g, sx, sy, x + ANCHO + 26, sy);
                    g.setFont(Tema.TEXTO_NEGRITA);
                    g.setColor(Tema.GRIS);
                    g.drawString("null", x + ANCHO + 30, sy + 5);
                } else if (col == porFila - 1) {
                    int siguienteY = y + ALTO + 50;
                    int yMedio = y + ALTO + 25;
                    Path2D camino = new Path2D.Double();
                    camino.moveTo(sx, sy);
                    camino.lineTo(x + ANCHO + 16, sy);
                    camino.lineTo(x + ANCHO + 16, yMedio);
                    camino.lineTo(margenX - 18, yMedio);
                    camino.lineTo(margenX - 18, siguienteY + ALTO / 2);
                    g.draw(camino);
                    PanelInicio.flecha(g, margenX - 18, siguienteY + ALTO / 2, margenX - 2, siguienteY + ALTO / 2);
                } else {
                    PanelInicio.flecha(g, sx, sy, x + ANCHO + separacion - 2, sy);
                }
            }
            g.dispose();
        }

        private void dibujarNodo(Graphics2D g, ItemCarrito item, int x, int y, boolean activo, int numero) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(x, y, ANCHO, ALTO, 12, 12);
            g.setColor(Tema.aclarar(acento, activo ? 0.6 : 0.88));
            g.fillRoundRect(x, y, ANCHO - 22, 24, 12, 12);
            g.fillRect(x, y + 12, ANCHO - 22, 12);
            g.setColor(activo ? acento : Tema.LINEA);
            g.setStroke(new BasicStroke(activo ? 3f : 1.4f));
            g.drawRoundRect(x, y, ANCHO, ALTO, 12, 12);
            g.drawLine(x + ANCHO - 22, y, x + ANCHO - 22, y + ALTO);
            g.setColor(Tema.TINTA);
            g.fillOval(x + ANCHO - 15, y + ALTO / 2 - 4, 8, 8);

            g.setFont(Tema.TEXTO_NEGRITA);
            g.setColor(Tema.TINTA);
            g.drawString(Tema.recortar(g, "#" + item.getProducto().getId() + "  " + item.getProducto().getNombre(),
                    ANCHO - 34), x + 10, y + 17);
            g.setFont(Tema.PEQUENA);
            g.setColor(Tema.GRIS);
            g.drawString("Cantidad: " + item.getCantidad() + "  ·  " + Tema.pesos(item.getProducto().getPrecio()),
                    x + 10, y + 42);
            g.setFont(Tema.TEXTO_NEGRITA.deriveFont(Font.BOLD, 13f));
            g.setColor(acento);
            g.drawString("Subtotal " + Tema.pesos(item.getSubtotal()), x + 10, y + 64);
            g.setFont(Tema.PEQUENA);
            g.setColor(Tema.GRIS_CLARO);
            g.drawString("nodo " + numero, x + 6, y + ALTO + 14);
        }
    }
}
