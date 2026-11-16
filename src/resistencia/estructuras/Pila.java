package resistencia.estructuras;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Pila (LIFO) genérica implementada sobre un arreglo dinámico.
 * Estructura de datos usada para almacenar las resistencias de un circuito.
 */
public class Pila<T> {

    private final List<T> elementos = new ArrayList<>();

    public void apilar(T elemento) {
        elementos.add(elemento);
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        return elementos.remove(elementos.size() - 1);
    }

    public T verTope() {
        if (estaVacia()) {
            throw new EmptyStackException();
        }
        return elementos.get(elementos.size() - 1);
    }

    public boolean estaVacia() {
        return elementos.isEmpty();
    }

    public int tamano() {
        return elementos.size();
    }

    /** Copia de los elementos (tope primero) para recorrerlos sin desapilar. */
    public List<T> aLista() {
        List<T> copia = new ArrayList<>(elementos);
        java.util.Collections.reverse(copia);
        return copia;
    }
}
