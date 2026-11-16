package rutamagica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Algoritmo de Dijkstra desde un solo origen.
 * Al construir el objeto se calculan las distancias mínimas del origen a
 * todos los pueblos; después se puede pedir la ruta hacia cualquier destino.
 */
public class Dijkstra {
    /** Distancia mínima conocida desde el origen hasta cada pueblo. */
    private Map<String, Double> distancia;
    /** Pueblo desde el que se llegó a cada pueblo por el mejor camino. */
    private Map<String, String> anterior;
    /** Orden en que salieron los pueblos de la cola y distancias en ese momento. */
    private final List<Paso> pasos = new ArrayList<>();

    /** Un paso del algoritmo: el pueblo que salió de la cola y la tabla de distancias. */
    public static class Paso {
        private final String pueblo;
        private final Map<String, Double> distancias;

        Paso(String pueblo, Map<String, Double> distancias) {
            this.pueblo = pueblo;
            this.distancias = distancias;
        }

        /** @return el pueblo que salió de la cola en este paso */
        public String getPueblo() {
            return pueblo;
        }

        /** @return copia de las distancias después de relajar sus carreteras */
        public Map<String, Double> getDistancias() {
            return distancias;
        }
    }

    /**
     * Ejecuta Dijkstra sobre el grafo usando una cola de prioridad.
     *
     * @param grafo  grafo de pueblos y carreteras
     * @param origen clave del pueblo de salida
     */
    public Dijkstra(Grafo grafo, String origen) {
        distancia = new HashMap<>();
        anterior = new HashMap<>();
        // Todos empiezan en infinito; el origen en 0.
        for (String pueblo : grafo.getPueblos()) {
            distancia.put(pueblo, Double.POSITIVE_INFINITY);
        }
        distancia.put(origen, 0.0);

        // Cada entrada es {pueblo, distancia acumulada}; sale primero la menor.
        PriorityQueue<Object[]> cola = new PriorityQueue<>((x, y) -> Double.compare((double) x[1], (double) y[1]));
        cola.add(new Object[] { origen, 0.0 });

        while (!cola.isEmpty()) {
            Object[] actual = cola.poll();
            String pueblo = (String) actual[0];
            double dist = (double) actual[1];
            // Entrada vieja: ya se encontró un camino más corto a este pueblo.
            if (dist > distancia.get(pueblo)) {
                continue;
            }
            // Relajación: ¿es más corto llegar al vecino pasando por este pueblo?
            for (Arista a : grafo.getVecinos(pueblo)) {
                double nueva = dist + a.getDistancia();
                if (nueva < distancia.get(a.getDestino())) {
                    distancia.put(a.getDestino(), nueva);
                    anterior.put(a.getDestino(), pueblo);
                    cola.add(new Object[] { a.getDestino(), nueva });
                }
            }
            pasos.add(new Paso(pueblo, new java.util.LinkedHashMap<>(distancia)));
        }
    }

    /**
     * @param destino clave del pueblo de llegada
     * @return distancia mínima en km (infinito si no hay ruta)
     */
    public double getDistancia(String destino) {
        return distancia.get(destino);
    }

    /**
     * Reconstruye la ruta siguiendo "anterior" desde el destino hasta el origen.
     *
     * @param destino clave del pueblo de llegada
     * @return pueblos de origen a destino, o lista vacía si no hay ruta
     */
    public List<String> getRuta(String destino) {
        List<String> ruta = new ArrayList<>();
        if (distancia.get(destino) == Double.POSITIVE_INFINITY) {
            return ruta;
        }
        for (String p = destino; p != null; p = anterior.get(p)) {
            ruta.add(p);
        }
        Collections.reverse(ruta);
        return ruta;
    }

    /** @return los pasos del algoritmo, en el orden en que se procesaron los pueblos */
    public List<Paso> getPasos() {
        return pasos;
    }
}
