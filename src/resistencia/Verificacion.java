package resistencia;

import resistencia.modelo.Resistencia;

/**
 * Comprueba los ejemplos de entrada/salida del enunciado sin depender
 * de un framework de pruebas externo.
 */
public class Verificacion {

    public static void main(String[] args) {
        verificar(160, "Marrón, Azul, Marrón");
        verificar(5800, "Verde, Gris, Rojo");
        verificar(10, "Marrón, Negro, Negro");
    }

    private static void verificar(long valor, String esperado) {
        Resistencia r = new Resistencia(valor);
        String obtenido = r.getBandaDigito1() + ", " + r.getBandaDigito2() + ", " + r.getBandaMultiplicador();
        String resultado = obtenido.equals(esperado) ? "PASA" : "FALLA";
        System.out.println(resultado + " -> valor=" + valor + " esperado=[" + esperado + "] obtenido=[" + obtenido + "]");
    }
}
