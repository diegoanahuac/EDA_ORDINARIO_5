package cola;

//Aranza Aguilar Madrigal , Ariana Belén Chinchilla Salazar , Diego Kamil Olea Ruiz, Erwin Deangelo Torres.
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cola de impresión FIFO con capacidad fija. Los trabajos se agregan al final
 * y se atienden por el frente. Se implementa sobre una lista del lenguaje
 * (ArrayList), como permite el enunciado.
 */
public class ColaImpresion {
    /** Capacidad máxima de la cola definida por el equipo. */
    public static final int CAPACIDAD = 6;
    /** Computadoras que pueden enviar trabajos. */
    public static final String[] COMPUTADORAS = { "PC1", "PC2", "PC3", "PC4", "PC5" };

    private final ArrayList<String> cola;
    private final Random random;

    /** Crea una cola vacía. */
    public ColaImpresion() {
        cola = new ArrayList<>();
        random = new Random();
    }

    /**
     * Agrega al final de la cola un trabajo de una computadora elegida al azar.
     *
     * @return la computadora que envió el trabajo, o null si la cola está llena
     */
    public String encolarAleatorio() {
        return encolar(COMPUTADORAS[random.nextInt(COMPUTADORAS.length)]);
    }

    /**
     * Agrega al final de la cola un trabajo de la computadora indicada.
     *
     * @param computadora nombre de la computadora (PC1 a PC5)
     * @return la computadora, o null si la cola está llena
     */
    public String encolar(String computadora) {
        if (estaLlena()) {
            return null;
        }
        cola.add(computadora);
        return computadora;
    }

    /**
     * Atiende (elimina) el trabajo del frente de la cola.
     *
     * @return la computadora atendida, o null si la cola está vacía
     */
    public String atender() {
        if (estaVacia()) {
            return null;
        }
        return cola.remove(0);
    }

    /** @return true si la cola alcanzó su capacidad */
    public boolean estaLlena() {
        return cola.size() >= CAPACIDAD;
    }

    /** @return true si no hay trabajos pendientes */
    public boolean estaVacia() {
        return cola.isEmpty();
    }

    /** @return copia de los trabajos pendientes, del frente al final */
    public List<String> getTrabajos() {
        return new ArrayList<>(cola);
    }

    /** Versión de consola: agrega un trabajo aleatorio y muestra la cola. */
    public void agregarTrabajo() {
        String computadora = encolarAleatorio();
        if (computadora == null) {
            System.out.println("La cola de impresion esta LLENA");
        } else {
            System.out.println("Trabajo agregado desde " + computadora);
            System.out.println("Cola actual: " + cola);
        }
    }

    /** Versión de consola: imprime el documento del frente. */
    public void imprimirDocumento() {
        String computadoraAtendida = atender();
        if (computadoraAtendida == null) {
            System.out.println("La cola de impresion esta VACIA");
        } else {
            System.out.println("Imprimiendo documento de " + computadoraAtendida);
            System.out.println("Cola actual: " + cola);
        }
    }

    /** Versión de consola: muestra los trabajos pendientes en orden de atención. */
    public void mostrarCola() {
        if (cola.isEmpty()) {
            System.out.println("La cola de impresion esta VACIA");
        } else {
            System.out.println("Trabajos pendientes");
            for (int i = 0; i < cola.size(); i++) {
                System.out.println((i + 1) + ". " + cola.get(i));
            }
        }
    }
}
