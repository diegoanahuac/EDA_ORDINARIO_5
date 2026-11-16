package resistencia.modelo;

import resistencia.estructuras.Pila;

import java.util.List;
import java.util.Random;

/**
 * Clase base de un circuito de n resistencias generadas aleatoriamente
 * (valores enteros entre 10 y 1 000 000 000 ohmios). Las subclases definen
 * cómo se calcula la resistencia total (serie o paralelo).
 */
public abstract class CircuitoResistencias {

    protected static final long VALOR_MINIMO = 10;
    protected static final long VALOR_MAXIMO = 1_000_000_000L;

    protected final Pila<Resistencia> resistencias = new Pila<>();
    protected final int n;

    protected CircuitoResistencias(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("La cantidad de resistencias (n) debe ser mayor a 0");
        }
        this.n = n;
        generarResistencias();
    }

    private void generarResistencias() {
        Random aleatorio = new Random();
        for (int i = 0; i < n; i++) {
            long valor = VALOR_MINIMO + (long) (aleatorio.nextDouble() * (VALOR_MAXIMO - VALOR_MINIMO));
            resistencias.apilar(new Resistencia(valor));
        }
    }

    public List<Resistencia> getResistencias() {
        return resistencias.aLista();
    }

    public abstract double calcularTotal();

    public abstract String getTipo();
}
