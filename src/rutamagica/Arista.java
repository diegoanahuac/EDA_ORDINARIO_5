package rutamagica;

/**
 * Arista del grafo: una carretera que sale de un pueblo y llega a otro.
 * El pueblo de origen no se guarda aquí porque la arista vive dentro de la
 * lista de adyacencia de ese pueblo.
 */
public class Arista {
    /** Pueblo al que llega la carretera. */
    private String destino;
    /** Longitud de la carretera en kilómetros (peso de la arista). */
    private double distancia;

    /**
     * Crea una carretera hacia otro pueblo.
     *
     * @param destino   clave del pueblo de llegada
     * @param distancia kilómetros de la carretera
     */
    public Arista(String destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }

    /** @return la clave del pueblo de llegada */
    public String getDestino() {
        return destino;
    }

    /** @return los kilómetros de la carretera */
    public double getDistancia() {
        return distancia;
    }
}
