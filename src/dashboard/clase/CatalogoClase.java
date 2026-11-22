package dashboard.clase;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Qué funciones de cada proyecto se muestran en la pestaña "Código y clase" y
 * con qué tema del curso se relacionan. Los temas y materiales son los del
 * módulo Contenido de Brightspace (Parcial 1 y Parcial 2) de Estructuras de
 * Datos y Algoritmos.
 */
public final class CatalogoClase {
    private static final String P1 = "Parcial 1";
    private static final String P2 = "Parcial 2";

    // Temas de Brightspace › Contenido.
    static final String T_ARREGLOS = "Parcial 1 › Arreglos";
    static final String T_LISTAS = "Parcial 1 › Listas enlazadas";
    static final String T_PILAS = "Parcial 1 › Pilas";
    static final String T_COLAS = "Parcial 1 › Colas";
    static final String T_ARBOLES = "Parcial 2 › 6. Árboles";
    static final String T_GRAFOS = "Parcial 2 › 7. Grafos";
    static final String T_ORDENAMIENTO = "Parcial 2 › 8. Ordenamiento y búsqueda";

    // Materiales de cada tema.
    static final String M_ARREGLOS = "Presentación de Arreglos";
    static final String M_LISTAS = "Presentación de Listas";
    static final String M_PILAS = "Presentación de Pilas";
    static final String M_COLAS = "Presentación de Colas";
    static final String M_ARBOLES = "Árboles2.pdf · Arboles APUNTES · Simulador de árbol binario";
    static final String M_GRAFOS = "MD5. Grafos_Liz.pdf · MD5. Grafos dirigidos_Liz";
    static final String M_CAMINO = "El camino mas corto.pdf";

    private static final Map<Integer, List<Referencia>> REFERENCIAS = Map.of(
            1, List.of(
                    new Referencia("Arreglo de colores: el índice es el dígito",
                            P1, T_ARREGLOS, M_ARREGLOS,
                            "resistencia/modelo/ColorResistencia.java", "public static ColorResistencia porIndice",
                            "values() regresa el arreglo de las 10 constantes del enum en orden, así que el "
                                    + "índice 0..9 es directamente el dígito o el exponente de la banda. Es el acceso "
                                    + "directo por índice que vimos en clase: tiempo constante O(1), y antes de leer se "
                                    + "valida el rango (0 a length - 1) para no salirse del arreglo."),
                    new Referencia("De un número a sus tres bandas",
                            P1, T_ARREGLOS, M_ARREGLOS,
                            "resistencia/modelo/Resistencia.java", "public Resistencia(long valorOhmios)",
                            "El valor se convierte en un arreglo de caracteres (sus dígitos) y se leen las "
                                    + "posiciones 0 y 1. La cantidad de dígitos que sobran es el exponente. Después, "
                                    + "cada número se usa como índice del arreglo de colores con porIndice."),
                    new Referencia("Generar n resistencias en una pila",
                            P1, T_PILAS, M_PILAS,
                            "resistencia/modelo/CircuitoResistencias.java", "private void generarResistencias()",
                            "Un ciclo for crea n resistencias con valores aleatorios y cada una se apila "
                                    + "(push) en la Pila del circuito. La pila crece por la cima; después aLista() la "
                                    + "entrega como lista para dibujarla y calcular el total."),
                    new Referencia("Total en paralelo: recorrer y acumular",
                            P1, T_ARREGLOS, M_ARREGLOS,
                            "resistencia/modelo/CircuitoParalelo.java", "public double calcularTotal()",
                            "Recorrido secuencial con for-each: se acumula la suma de inversos y al final se "
                                    + "invierte (1/Rt = 1/R1 + 1/R2 + ...). Es el patrón de recorrido y acumulador "
                                    + "que vimos con arreglos, O(n).")),
            2, List.of(
                    new Referencia("Insertar al final de la lista",
                            P1, T_LISTAS, M_LISTAS,
                            "carrito/Carrito.java", "public void agregarProducto",
                            "Si la lista está vacía el nuevo nodo es la cabeza; si no, se recorre con un "
                                    + "apuntador 'actual' hasta el último nodo (siguiente == null) y se enlaza ahí. "
                                    + "Antes se busca el producto: si ya existe, sólo se suma la cantidad al mismo nodo."),
                    new Referencia("Eliminar un nodo por ID",
                            P1, T_LISTAS, M_LISTAS,
                            "carrito/Carrito.java", "public boolean eliminarPorId",
                            "Se avanza con dos apuntadores, 'anterior' y 'actual'. Si el nodo a borrar es la "
                                    + "cabeza, la cabeza pasa al siguiente; si está en medio o al final, el anterior "
                                    + "se salta el nodo (anterior.siguiente = actual.siguiente). Son los dos casos de "
                                    + "eliminación vistos en clase."),
                    new Referencia("Recorrer la lista para el total",
                            P1, T_LISTAS, M_LISTAS,
                            "carrito/Carrito.java", "public double calcularTotal()",
                            "Recorrido clásico de lista enlazada: empezar en la cabeza y avanzar con "
                                    + "actual = actual.getSiguiente() hasta llegar a null, acumulando el subtotal de "
                                    + "cada nodo. O(n).")),
            3, List.of(
                    new Referencia("Push: apilar en la cima",
                            P1, T_PILAS, M_PILAS,
                            "navegador/modelo/Pila.java", "public void apilar(T elemento)",
                            "Pila implementada con un arreglo y un índice 'tope'. Apilar escribe en "
                                    + "elementos[tope] y aumenta el tope; si la pila está llena se lanza un error "
                                    + "(desbordamiento). Es la operación push de clase, O(1)."),
                    new Referencia("Pop: desapilar la cima",
                            P1, T_PILAS, M_PILAS,
                            "navegador/modelo/Pila.java", "public T desapilar()",
                            "LIFO: sale el último que entró. Se baja el tope y se regresa ese elemento; si la "
                                    + "pila está vacía se avisa (subdesbordamiento). La posición se pone en null para "
                                    + "liberar la referencia."),
                    new Referencia("Back con dos pilas",
                            P1, T_PILAS, M_PILAS,
                            "navegador/modelo/Navegador.java", "public Resultado atras()",
                            "Aplicación de pilas: la página actual se apila en 'adelante' y la nueva página "
                                    + "actual sale de 'atrás' con pop. Forward hace lo inverso, y visitar una página "
                                    + "nueva vacía la pila adelante.")),
            4, List.of(
                    new Referencia("Encolar: entra por el final",
                            P1, T_COLAS, M_COLAS,
                            "cola/ColaImpresion.java", "public String encolar(String computadora)",
                            "FIFO: el trabajo nuevo se agrega al final de la cola, y antes se revisa si ya está "
                                    + "llena (capacidad 6). Es la operación encolar (enqueue) de clase."),
                    new Referencia("Atender: sale el frente",
                            P1, T_COLAS, M_COLAS,
                            "cola/ColaImpresion.java", "public String atender()",
                            "Desencolar (dequeue): se atiende el primer trabajo que llegó, el de la posición 0. "
                                    + "Si la cola está vacía no hay nada que imprimir y se avisa.")),
            5, List.of(
                    new Referencia("Insertar en el árbol binario de búsqueda",
                            P2, T_ARBOLES, M_ARBOLES,
                            "archivos/ArbolArchivos.java", "public void insertar",
                            "Regla del ABB vista en clase: se compara con la raíz; si el nombre es menor se baja "
                                    + "a la izquierda y si es mayor a la derecha, hasta encontrar un hueco (null) donde "
                                    + "va el nodo nuevo. No se permiten duplicados."),
                    new Referencia("Buscar en el ABB",
                            P2, T_ARBOLES, M_ARBOLES,
                            "archivos/ArbolArchivos.java", "public Nodo buscar(String nombre)",
                            "En cada nodo se decide izquierda o derecha y se descarta la otra mitad, por eso un "
                                    + "árbol equilibrado se busca en O(log n). Si se llega a null, el elemento no existe."),
                    new Referencia("Recorrido inorden (recursivo)",
                            P2, T_ARBOLES, M_ARBOLES,
                            "archivos/ArbolArchivos.java", "private void inorden(Nodo actual",
                            "Izquierda, raíz, derecha: en un ABB entrega los nombres en orden alfabético. Es la "
                                    + "versión recursiva de clase, con el caso base cuando el nodo es null. Preorden y "
                                    + "postorden sólo cambian el lugar donde se visita la raíz."),
                    new Referencia("Eliminar: los tres casos",
                            P2, T_ARBOLES, M_ARBOLES,
                            "archivos/ArbolArchivos.java", "public boolean eliminar(String nombre)",
                            "Los tres casos de clase: hoja (se quita), un hijo (el hijo toma su lugar) y dos "
                                    + "hijos (se reemplaza por el sucesor inorden, el menor del subárbol derecho, o por "
                                    + "el predecesor si el sucesor es un archivo).")),
            6, List.of(
                    new Referencia("Grafo con lista de adyacencia",
                            P2, T_GRAFOS, M_GRAFOS,
                            "rutamagica/Grafo.java", "public void agregarCarretera",
                            "Cada pueblo es un vértice y guarda la lista de sus aristas. Como el grafo es no "
                                    + "dirigido, la carretera se agrega en los dos sentidos (a→b y b→a) con su peso en "
                                    + "kilómetros: es un grafo ponderado."),
                    new Referencia("Dijkstra: el camino más corto",
                            P2, T_GRAFOS, M_CAMINO,
                            "rutamagica/Dijkstra.java", "public Dijkstra(Grafo grafo, String origen)",
                            "Como en clase: todos los vértices empiezan en infinito y el origen en 0; se toma el "
                                    + "de menor distancia no visitado y se relajan sus vecinos (si pasar por él es más "
                                    + "corto, se actualiza la distancia y el anterior). La cola de prioridad sólo hace "
                                    + "más rápido elegir el menor."),
                    new Referencia("Reconstruir la ruta",
                            P2, T_GRAFOS, M_CAMINO,
                            "rutamagica/Dijkstra.java", "public List<String> getRuta(String destino)",
                            "Se sigue el arreglo 'anterior' desde el destino hasta el origen y luego se invierte "
                                    + "la lista. Si la distancia quedó en infinito, no hay ruta entre los dos pueblos."),
                    new Referencia("Leer el grafo desde un archivo",
                            P2, T_GRAFOS, M_GRAFOS,
                            "rutamagica/Grafo.java", "public static Grafo desdeLineas",
                            "Cada línea válida 'PuebloA PuebloB km' se convierte en una arista. Las líneas mal "
                                    + "formadas o con distancia negativa se ignoran con try-catch, para que el grafo no "
                                    + "tenga pesos inválidos (Dijkstra no admite pesos negativos).")));

    private CatalogoClase() {
    }

    /**
     * @param numero número del proyecto (1 a 6)
     * @return las referencias de ese proyecto (vacío si no hay)
     */
    public static List<Referencia> de(int numero) {
        return REFERENCIAS.getOrDefault(numero, Collections.emptyList());
    }
}
