package dashboard;

import dashboard.proyectos.ProyectoArchivos;
import dashboard.proyectos.ProyectoCola;
import dashboard.proyectos.ProyectoNavegador;
import dashboard.proyectos.ProyectoResistencia;
import dashboard.proyectos.ProyectoRutaMagica;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Genera las capturas de la documentación: abre el dashboard, pone cada
 * proyecto en un estado de prueba y guarda la ventana como imagen PNG.
 * Uso: java -cp out dashboard.Capturas docs/capturas
 */
public final class Capturas {
    private static Dashboard dashboard;

    private Capturas() {
    }

    /**
     * @param args carpeta de salida (por defecto docs/capturas)
     * @throws Exception si no se puede crear la ventana o guardar una imagen
     */
    public static void main(String[] args) throws Exception {
        File carpeta = new File(args.length > 0 ? args[0] : "docs/capturas");
        carpeta.mkdirs();
        SwingUtilities.invokeAndWait(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                // Se usa el look and feel por defecto.
            }
            dashboard = new Dashboard();
            dashboard.setSize(1440, 900);
            dashboard.setVisible(true);
        });
        List<PanelProyecto> p = dashboard.getProyectos();
        ProyectoResistencia resistencia = (ProyectoResistencia) p.get(0);
        ProyectoNavegador navegador = (ProyectoNavegador) p.get(2);
        ProyectoCola cola = (ProyectoCola) p.get(3);
        ProyectoArchivos archivos = (ProyectoArchivos) p.get(4);
        ProyectoRutaMagica ruta = (ProyectoRutaMagica) p.get(5);

        capturar(carpeta, "00_inicio", Dashboard.INICIO, null, 0);

        capturar(carpeta, "01_resistencia_paralelo", Dashboard.clave(1), () -> resistencia.mostrar(5800, 5, false), 0);
        capturar(carpeta, "01_resistencia_serie", Dashboard.clave(1), () -> resistencia.mostrar(160, 6, true), 0);

        capturar(carpeta, "02_carrito", Dashboard.clave(2), () -> p.get(1).cargarEjemplo(), 0);

        capturar(carpeta, "03_navegador", Dashboard.clave(3), navegador::cargarEjemplo, 0);
        capturar(carpeta, "03_navegador_404", Dashboard.clave(3),
                () -> navegador.visitar("https://www.pagina-que-no-existe.com"), 0);

        capturar(carpeta, "04_cola", Dashboard.clave(4), () -> {
            cola.cargarEjemplo();
            cola.imprimir();
        }, 1200);
        capturar(carpeta, "04_cola_llena", Dashboard.clave(4), () -> {
            cola.agregar("PC4");
            cola.agregar("PC1");
            cola.agregar("PC2");
        }, 1200);

        capturar(carpeta, "05_archivos_busqueda", Dashboard.clave(5), () -> {
            archivos.cargarEjemplo();
            archivos.buscar("Gatos");
        }, 0);
        capturar(carpeta, "05_archivos_inorden", Dashboard.clave(5),
                () -> archivos.iniciarRecorrido("Inorden", false), 0);
        capturar(carpeta, "05_archivos_eliminar_caso3", Dashboard.clave(5), () -> {
            archivos.cargarEjemplo();
            archivos.eliminar("Fotos", false);
        }, 0);

        capturar(carpeta, "06_ruta_sisal_espita", Dashboard.clave(6), ruta::cargarEjemplo, 0);
        capturar(carpeta, "06_ruta_paso_a_paso", Dashboard.clave(6),
                () -> ruta.consultar("Tekax", "Motul", true), 2900);

        SwingUtilities.invokeAndWait(() -> dashboard.dispose());
        System.out.println("Capturas guardadas en " + carpeta.getPath());
        System.exit(0);
    }

    private static void capturar(File carpeta, String nombre, String pantalla, Runnable preparar, long espera)
            throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            dashboard.mostrar(pantalla);
            if (preparar != null) {
                preparar.run();
            }
        });
        Thread.sleep(400 + espera);
        SwingUtilities.invokeAndWait(() -> {
            try {
                int w = dashboard.getContentPane().getWidth();
                int h = dashboard.getContentPane().getHeight();
                BufferedImage imagen = new BufferedImage(w * 2, h * 2, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = imagen.createGraphics();
                g.scale(2, 2);
                dashboard.getContentPane().paint(g);
                g.dispose();
                ImageIO.write(imagen, "png", new File(carpeta, nombre + ".png"));
                System.out.println("  " + nombre + ".png");
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
