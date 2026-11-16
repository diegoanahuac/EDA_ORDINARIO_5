package rutamagica;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

/**
 * Programa principal de Ruta Mágica (Yucatán).
 * Muestra los Pueblos Mágicos, pide origen y destino, calcula la ruta más
 * corta con Dijkstra e imprime cada tramo, la ruta completa y la distancia total.
 */
public class RutaMagica {
    /** Separador de la salida en consola. */
    private static final String LINEA = "================================================================";
    /** Clave corta del archivo -> nombre completo del Pueblo Mágico. */
    private static final Map<String, String> NOMBRES = new HashMap<>();

    static {
        NOMBRES.put("Izamal", "Izamal");
        NOMBRES.put("Motul", "Motul de Carrillo Puerto");
        NOMBRES.put("Valladolid", "Valladolid");
        NOMBRES.put("Espita", "Espita");
        NOMBRES.put("Mani", "Mani");
        NOMBRES.put("Sisal", "Sisal");
        NOMBRES.put("Tekax", "Tekax de Alvaro Obregon");
    }

    /**
     * Punto de entrada. Carga el grafo y repite consultas mientras el usuario responda S.
     *
     * @param args opcional: ruta de otro archivo de grafo
     */
    public static void main(String[] args) {
        String archivo = args.length > 0 ? args[0] : "pueblos_yucatan.txt";
        Grafo grafo;
        try {
            grafo = Grafo.desdeArchivo(archivo);
        } catch (IOException e) {
            System.out.println("No se pudo leer el archivo " + archivo + ": " + e.getMessage());
            return;
        }

        List<String> pueblos = grafo.getPueblos();
        Scanner sc = new Scanner(System.in);
        String respuesta;

        do {
            System.out.println(LINEA);
            System.out.println("        RUTA MAGICA -- YUCATAN");
            System.out.println("   Camino mas corto entre Pueblos Magicos");
            System.out.println(LINEA);
            System.out.println("Pueblos Magicos disponibles:");
            for (int i = 0; i < pueblos.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + nombre(pueblos.get(i)));
            }

            String origen = pedirPueblo(sc, pueblos, "ORIGEN");
            System.out.println("  -> Origen: " + nombre(origen));
            String destino = pedirPueblo(sc, pueblos, "DESTINO");
            System.out.println("  -> Destino: " + nombre(destino));

            System.out.println();
            System.out.println("Algoritmo utilizado: Dijkstra");
            System.out.println("Calculando ruta mas corta...");

            Dijkstra dijkstra = new Dijkstra(grafo, origen);
            List<String> ruta = dijkstra.getRuta(destino);

            System.out.println(LINEA);
            if (ruta.isEmpty()) {
                System.out.println("  [X] NO EXISTE RUTA");
            } else if (origen.equals(destino)) {
                System.out.println("  [OK] ORIGEN Y DESTINO SON EL MISMO PUEBLO");
                System.out.println(LINEA);
                System.out.println("  Distancia total: 0 km");
            } else {
                System.out.println("  [OK] RUTA ENCONTRADA");
                System.out.println(LINEA);
                imprimirRuta(grafo, ruta, dijkstra.getDistancia(destino));
            }
            System.out.println(LINEA);

            System.out.print("Desea consultar otra ruta? (S/N): ");
            respuesta = sc.nextLine().trim();
            System.out.println();
        } while (respuesta.equalsIgnoreCase("S"));

        System.out.println("Gracias por usar Ruta Magica. Buen viaje!");
    }

    /**
     * Pide un número de pueblo hasta que el usuario escriba uno válido.
     *
     * @param sc      lector de la consola
     * @param pueblos lista de pueblos del menú
     * @param tipo    "ORIGEN" o "DESTINO"
     * @return la clave del pueblo elegido
     */
    private static String pedirPueblo(Scanner sc, List<String> pueblos, String tipo) {
        while (true) {
            System.out.print("Seleccione pueblo de " + tipo + " (1-" + pueblos.size() + "): ");
            String entrada = sc.nextLine().trim();
            try {
                int opcion = Integer.parseInt(entrada);
                if (opcion >= 1 && opcion <= pueblos.size()) {
                    return pueblos.get(opcion - 1);
                }
            } catch (NumberFormatException e) {
                // No es un número: se trata igual que una opción fuera de rango.
            }
            System.out.println("  Opcion invalida, intente de nuevo.");
        }
    }

    /**
     * Imprime cada tramo con sus km, la ruta completa y la distancia total.
     *
     * @param grafo grafo para consultar el peso de cada tramo
     * @param ruta  pueblos de origen a destino
     * @param total distancia total en km
     */
    private static void imprimirRuta(Grafo grafo, List<String> ruta, double total) {
        int ancho = 0;
        for (String p : ruta) {
            ancho = Math.max(ancho, p.length());
        }
        for (int i = 0; i < ruta.size() - 1; i++) {
            String a = ruta.get(i);
            String b = ruta.get(i + 1);
            System.out.printf("  %-" + ancho + "s --(%6s km)--> %s%n", a, km(peso(grafo, a, b)), b);
        }
        System.out.println();
        System.out.println("  Ruta completa:");
        System.out.println("  " + String.join(" -> ", ruta));
        System.out.println("  Distancia total: " + km(total) + " km");
    }

    /**
     * @return los km de la carretera directa entre a y b (la menor si hay varias)
     */
    private static double peso(Grafo grafo, String a, String b) {
        double menor = Double.POSITIVE_INFINITY;
        for (Arista arista : grafo.getVecinos(a)) {
            if (arista.getDestino().equals(b)) {
                menor = Math.min(menor, arista.getDistancia());
            }
        }
        return menor;
    }

    /**
     * Da formato a los km: sin decimales si es entero, con uno si no.
     */
    private static String km(double valor) {
        if (valor == Math.floor(valor)) {
            return String.valueOf((long) valor);
        }
        return String.format(Locale.US, "%.1f", valor);
    }

    /**
     * @return el nombre completo del pueblo, o la clave si no está en el mapa
     */
    private static String nombre(String clave) {
        return NOMBRES.getOrDefault(clave, clave);
    }
}
