package cola;

import java.util.Scanner;

public class Main {
     public static void main (String[] args) {
        ColaImpresion cola = new ColaImpresion();

        Scanner scanner = new Scanner(System.in);
        int opcion;

        do { 
            System.out.println("\n Cola de impresión ");
            System.out.println("1. Agregar trabajo");
            System.out.println("2. Imprimir documento");
            System.out.println("3. Mostrar Cola");
            System.out.println("4. Salir");
            System.out.println("Selecciona una opción: ");

            while (!scanner.hasNextInt()) {
                System.out.println("Entrada inválida. Ingresa un número.");
                scanner.next();
                System.out.println("Selecciona una opción");
            }

            opcion = scanner.nextInt();

            switch (opcion) {
                case 1: 
                cola.agregarTrabajo();
                break;

                case 2: 
                cola.imprimirDocumento();
                break;

                case 3:
                cola.mostrarCola();
                break;

                case 4:
                System.out.println("Saliendo...");
                break;

                default:
                System.out.println("Opción inválida.");
            }
        } while (opcion != 4);
        scanner.close();
     }
}