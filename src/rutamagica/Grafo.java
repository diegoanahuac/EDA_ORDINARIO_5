package rutamagica;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Grafo ponderado y no dirigido de los Pueblos Mágicos.
 * Se guarda como lista de adyacencia: a cada pueblo le corresponde la lista
 * de carreteras (aristas) que salen de él.
 */
public class Grafo {
    /** Lista de adyacencia. LinkedHashMap conserva el orden del archivo. */
    private Map<String, List<Arista>> adyacencia;

    /** Crea un grafo vacío. */
    public Grafo() {
        adyacencia = new LinkedHashMap<>();
    }

    /**
     * Agrega un pueblo (vértice) con su lista vacía, si todavía no existe.
     *
     * @param pueblo clave del pueblo
     */
    public void agregarPueblo(String pueblo) {
        adyacencia.putIfAbsent(pueblo, new ArrayList<>());
    }

    /**
     * Agrega una carretera en los dos sentidos, porque el grafo es no dirigido.
     *
     * @param a  pueblo de un extremo
     * @param b  pueblo del otro extremo
     * @param km distancia en kilómetros
     */
    public void agregarCarretera(String a, String b, double km) {
        agregarPueblo(a);
        agregarPueblo(b);
        adyacencia.get(a).add(new Arista(b, km));
        adyacencia.get(b).add(new Arista(a, km));
    }

    /**
     * @param pueblo clave del pueblo
     * @return las carreteras que salen del pueblo (lista vacía si no existe)
     */
    public List<Arista> getVecinos(String pueblo) {
        return adyacencia.getOrDefault(pueblo, new ArrayList<>());
    }

    /** @return los pueblos del grafo en el orden en que se cargaron */
    public List<String> getPueblos() {
        return new ArrayList<>(adyacencia.keySet());
    }

    /**
     * Lee el grafo desde un archivo de texto con líneas "PuebloA PuebloB km".
     *
     * @param ruta ruta del archivo
     * @return el grafo cargado
     * @throws IOException si el archivo no se puede leer
     */
    public static Grafo desdeArchivo(String ruta) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return desdeLineas(lineas);
    }

    /**
     * Construye el grafo a partir de líneas "PuebloA PuebloB km".
     * Ignora líneas vacías, comentarios (#) y líneas mal formadas.
     *
     * @param lineas contenido del archivo, una carretera por línea
     * @return el grafo cargado
     */
    public static Grafo desdeLineas(List<String> lineas) {
        Grafo grafo = new Grafo();
        for (String original : lineas) {
            String linea = original.trim();
            if (linea.isEmpty() || linea.startsWith("#")) {
                continue;
            }
            String[] partes = linea.split("\\s+");
            try {
                double km = Double.parseDouble(partes[2]);
                if (km < 0) {
                    throw new NumberFormatException("distancia negativa");
                }
                grafo.agregarCarretera(partes[0], partes[1], km);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                System.out.println("Linea ignorada (formato invalido): " + linea);
            }
        }
        return grafo;
    }
}
