package resistencia;

import resistencia.modelo.CircuitoParalelo;
import resistencia.modelo.CircuitoResistencias;
import resistencia.modelo.CircuitoSerie;
import resistencia.modelo.Resistencia;
import resistencia.vista.VentanaResistencia;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero(teclado, "Seleccione una opción: ");

            switch (opcion) {
                case 1:
                    calcularColores(teclado);
                    break;
                case 2:
                    calcularCircuito(teclado, true);
                    break;
                case 3:
                    calcularCircuito(teclado, false);
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 0);

        teclado.close();
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== Calculadora de resistencias =====");
        System.out.println("1) Determinar colores a partir de un valor");
        System.out.println("2) Resistencia total en serie");
        System.out.println("3) Resistencia total en paralelo");
        System.out.println("0) Salir");
    }

    private static void calcularColores(Scanner teclado) {
        long valor = leerLong(teclado, "Ingrese el valor de la resistencia (ohmios): ");
        Resistencia resistencia = new Resistencia(valor);
        System.out.println(resistencia);
        VentanaResistencia.mostrar(resistencia);
    }

    private static void calcularCircuito(Scanner teclado, boolean esSerie) {
        int n = leerEntero(teclado, "Ingrese la cantidad de resistencias (n): ");
        CircuitoResistencias circuito = esSerie ? new CircuitoSerie(n) : new CircuitoParalelo(n);

        System.out.println("Resistencias generadas:");
        for (Resistencia r : circuito.getResistencias()) {
            System.out.println("  " + r);
        }

        System.out.printf("Resistencia total en %s: %.4f ohmios%n", circuito.getTipo(), circuito.calcularTotal());
    }

    private static int leerEntero(Scanner teclado, String mensaje) {
        System.out.print(mensaje);
        while (!teclado.hasNextInt()) {
            System.out.print("Ingrese un número entero válido: ");
            teclado.next();
        }
        return teclado.nextInt();
    }

    private static long leerLong(Scanner teclado, String mensaje) {
        System.out.print(mensaje);
        while (!teclado.hasNextLong()) {
            System.out.print("Ingrese un número entero válido: ");
            teclado.next();
        }
        return teclado.nextLong();
    }
}
