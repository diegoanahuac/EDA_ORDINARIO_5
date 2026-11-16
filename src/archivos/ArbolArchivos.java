package archivos;

import java.util.ArrayList;
import java.util.List;

public class ArbolArchivos {
    private Nodo raiz;

    public ArbolArchivos(String nombreRaiz) {
        if (nombreRaiz == null || nombreRaiz.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del directorio raiz no puede estar vacio");
        }
        raiz = new Nodo(nombreRaiz.trim(), "", 0, true);
    }
    
    public Nodo getRaiz() {
        return raiz;
    }

    public void insertar(String nombre, String extension, long tamanio, boolean esDirectorio) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        nombre = nombre.trim();
        extension = extension == null ? "" : extension.trim();

        if (esDirectorio) {
            if(!extension.isEmpty()) {
                throw new IllegalArgumentException("Un directorio no debe tener extension");
            }
            tamanio = 0;
        } else {
            if (extension.isEmpty()) {
                throw new IllegalArgumentException("Un archivo debe tener extension");
            }
            if (tamanio <0) {
                throw new IllegalArgumentException("El tamano no puede ser negativo");
            }
        }
        Nodo actual = raiz;
        while (true) { 
            int comparacion = nombre.compareToIgnoreCase(actual.getNombre());
            if (comparacion == 0) {
                throw new IllegalArgumentException("Ya existe un elemento con ese nombre");
            }
            if (!actual.isDirectorio()) {
                throw new IllegalArgumentException("Un archivo no puede contener otros elementos");
            }
            if (comparacion < 0) {
                if (actual.getIzquierdo() == null) {
                    actual.setIzquierdo(new Nodo(nombre, extension, tamanio, esDirectorio));
                    return;
                }
                actual = actual.getIzquierdo();
            } else {
                if (actual.getDerecho() == null) {
                    actual.setDerecho(new Nodo(nombre, extension, tamanio, esDirectorio));
                    return;
                }
                actual = actual.getDerecho();
            }
        }
    }

    public Nodo buscar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        nombre = nombre.trim();
        Nodo actual = raiz;

        while (actual != null) {
            int comparacion =nombre.compareToIgnoreCase(actual.getNombre());
            if (comparacion == 0) {
                return actual;
            }
            if(comparacion < 0) {
                actual = actual.getIzquierdo();
            } else {
                actual = actual.getDerecho();
            }
        }
        return null;
    }
    public List<Nodo> inorden() {
        List<Nodo> resultado = new ArrayList<>();
        inorden(raiz, resultado);
        return resultado;
    }

    private void inorden(Nodo actual, List<Nodo> resultado) {
        if (actual == null) {
            return;
        }
        inorden(actual.getIzquierdo(), resultado);
        resultado.add(actual);
        inorden(actual.getDerecho(), resultado);
    }
    public List<Nodo> preorden() {
        List<Nodo> resultado = new ArrayList<>();
        preorden(raiz, resultado);
        return resultado;
    }

    private void preorden(Nodo actual, List<Nodo> resultado) {
        if (actual == null) {
            return;
        }
        resultado.add(actual);
        preorden(actual.getIzquierdo(), resultado);
        preorden(actual.getDerecho(), resultado);
    }
    public List<Nodo> postorden() {
        List<Nodo> resultado = new ArrayList<>();
        postorden(raiz, resultado);
        return resultado;
    }

    public void postorden(Nodo actual, List<Nodo> resultado) {
        if (actual == null) {
            return;
        }
        postorden(actual.getIzquierdo(), resultado);
        postorden(actual.getDerecho(), resultado);
        resultado.add(actual);
    }

    /**
     * Elimina un elemento con las tres situaciones de un ABB:
     * 1) nodo hoja: se quita directamente;
     * 2) nodo con un hijo: su único hijo ocupa su lugar;
     * 3) nodo con dos hijos: lo reemplaza su sucesor inorden (el menor del
     *    subárbol derecho). Si el sucesor es un archivo, se intenta con el
     *    predecesor inorden, porque un archivo siempre debe ser hoja.
     * La raíz no se puede eliminar.
     *
     * @param nombre nombre del elemento
     * @return true si se eliminó, false si no existe
     */
    public boolean eliminar(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        nombre = nombre.trim();
        if (nombre.compareToIgnoreCase(raiz.getNombre()) == 0) {
            throw new IllegalArgumentException("no se puede eliminar el directorio raiz");
        }
        Nodo actual = raiz;
        Nodo padre = null;
        while (actual != null) {
            int comparacion = nombre.compareToIgnoreCase(actual.getNombre());
            if (comparacion == 0) {
                break;
            }
            padre = actual;
            actual = comparacion < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        if (actual == null) {
            return false;
        }
        Nodo reemplazo;
        if (actual.getIzquierdo() == null || actual.getDerecho() == null) {
            // Situaciones 1 y 2: hoja o un solo hijo.
            reemplazo = actual.getIzquierdo() != null ? actual.getIzquierdo() : actual.getDerecho();
        } else {
            // Situación 3: dos hijos.
            reemplazo = extraerSucesor(actual);
            if (reemplazo == null) {
                reemplazo = extraerPredecesor(actual);
            }
            if (reemplazo == null) {
                throw new IllegalArgumentException(
                        "No se puede eliminar: el sucesor y el predecesor son archivos y un archivo no puede tener hijos");
            }
            reemplazo.setIzquierdo(actual.getIzquierdo());
            reemplazo.setDerecho(actual.getDerecho());
        }
        if (padre.getIzquierdo() == actual) {
            padre.setIzquierdo(reemplazo);
        } else {
            padre.setDerecho(reemplazo);
        }
        return true;
    }

    /**
     * Saca del árbol el sucesor inorden de un nodo con dos hijos, si es directorio.
     *
     * @param nodo nodo con dos hijos
     * @return el sucesor ya desenganchado, o null si es un archivo
     */
    private Nodo extraerSucesor(Nodo nodo) {
        Nodo padre = nodo;
        Nodo sucesor = nodo.getDerecho();
        while (sucesor.getIzquierdo() != null) {
            padre = sucesor;
            sucesor = sucesor.getIzquierdo();
        }
        if (!sucesor.isDirectorio()) {
            return null;
        }
        if (padre == nodo) {
            padre.setDerecho(sucesor.getDerecho());
        } else {
            padre.setIzquierdo(sucesor.getDerecho());
        }
        sucesor.setDerecho(null);
        return sucesor;
    }

    /**
     * Saca del árbol el predecesor inorden de un nodo con dos hijos, si es directorio.
     *
     * @param nodo nodo con dos hijos
     * @return el predecesor ya desenganchado, o null si es un archivo
     */
    private Nodo extraerPredecesor(Nodo nodo) {
        Nodo padre = nodo;
        Nodo predecesor = nodo.getIzquierdo();
        while (predecesor.getDerecho() != null) {
            padre = predecesor;
            predecesor = predecesor.getDerecho();
        }
        if (!predecesor.isDirectorio()) {
            return null;
        }
        if (padre == nodo) {
            padre.setIzquierdo(predecesor.getIzquierdo());
        } else {
            padre.setDerecho(predecesor.getIzquierdo());
        }
        predecesor.setIzquierdo(null);
        return predecesor;
    }

    /**
     * Nodos que recorre la búsqueda desde la raíz hasta el nombre buscado.
     *
     * @param nombre nombre buscado
     * @return los nodos visitados, en orden (el último es el encontrado si existe)
     */
    public List<Nodo> caminoBusqueda(String nombre) {
        List<Nodo> camino = new ArrayList<>();
        if (nombre == null || nombre.trim().isEmpty()) {
            return camino;
        }
        Nodo actual = raiz;
        while (actual != null) {
            camino.add(actual);
            int comparacion = nombre.trim().compareToIgnoreCase(actual.getNombre());
            if (comparacion == 0) {
                break;
            }
            actual = comparacion < 0 ? actual.getIzquierdo() : actual.getDerecho();
        }
        return camino;
    }

    /** @return la altura del árbol (la raíz sola tiene altura 1) */
    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo nodo) {
        return nodo == null ? 0 : 1 + Math.max(altura(nodo.getIzquierdo()), altura(nodo.getDerecho()));
    }
}
