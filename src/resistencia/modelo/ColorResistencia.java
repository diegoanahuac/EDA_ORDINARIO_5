package resistencia.modelo;

import java.awt.Color;
//Importa una biblioteca de Java incluido con JDK

/**
 * Representa cada color del código de bandas de una resistencia,
 * con su dígito/exponente asociado (tabla del enunciado) y el color
 * real usado para dibujarlo en pantalla
 */

//Arreglo primitivo guarda valores simples, ejemplo int, boolean, double, char
//Arreglo de Objetos que es el que usamos es para almacenar instancias de una clase, usado en ColorResistencia

//Clase enum(para datos especiales, que representan un conjunto fijo de constantes), funciona para definir los colores con "java.awt.Color", mas que nada para la parte visual
public enum ColorResistencia {
    NEGRO("Negro", new Color(20, 20, 20)),
    MARRON("Marrón", new Color(139, 69, 19)),
    ROJO("Rojo", new Color(200, 20, 20)),
    NARANJA("Naranja", new Color(255, 140, 0)),
    AMARILLO("Amarillo", new Color(230, 210, 0)),
    VERDE("Verde", new Color(0, 140, 0)),
    AZUL("Azul", new Color(0, 0, 200)),
    VIOLETA("Violeta", new Color(148, 0, 211)),
    GRIS("Gris", new Color(128, 128, 128)),
    BLANCO("Blanco", new Color(255, 255, 255));

    //private final String Define el nombre del color, y private final Color define el color que se mostrará
    //Se usa private para encapsular los atributos para solo usarse dentro de la clase ColorResistencia
    //Y final para que nunca cambie el valor, (dentro del constructor)
    private final String nombreMostrado;
    private final Color colorAwt;

    //Constructor de la clase ColorResistencia, crea el objeto color e inicializa con todos sus atributos
    ColorResistencia(String nombreMostrado, Color colorAwt) {
        this.nombreMostrado = nombreMostrado;
        this.colorAwt = colorAwt;
    }

    //getter, para leer el valor de un atributo privado
    public Color getColorAwt() {
        return colorAwt;
    }

    /**
     * El índice (0-9) coincide a la vez con el dígito que representa el color
     * y con el exponente de su multiplicador (10^indice), tal como la tabla
     * Negro=0, Marrón=1, ..., Blanco=9.
     */
    public static ColorResistencia porIndice(int indice) {
        ColorResistencia[] valores = values();
        if (indice < 0 || indice >= valores.length) {
            throw new IllegalArgumentException("Índice fuera de rango (0-9): " + indice);
        }
        return valores[indice];
    }

    @Override
    public String toString() {
        return nombreMostrado;
    }
}
