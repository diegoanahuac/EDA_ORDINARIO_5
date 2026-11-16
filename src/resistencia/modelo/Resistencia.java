package resistencia.modelo;

/**
 * Una resistencia eléctrica: su valor en ohmios y las tres bandas de color
 * (primer dígito, segundo dígito y multiplicador) que le corresponden.
 */
public class Resistencia {

    private final long valorOhmios;
    private final ColorResistencia bandaDigito1;
    private final ColorResistencia bandaDigito2;
    private final ColorResistencia bandaMultiplicador;

    public Resistencia(long valorOhmios) {
        if (valorOhmios <= 0) {
            throw new IllegalArgumentException("El valor de la resistencia debe ser mayor a 0");
        }
        this.valorOhmios = valorOhmios;

        String digitos = Long.toString(valorOhmios);
        int primerDigito = Character.getNumericValue(digitos.charAt(0));
        int segundoDigito = digitos.length() > 1 ? Character.getNumericValue(digitos.charAt(1)) : 0;
        int exponente = Math.max(digitos.length() - 2, 0);

        this.bandaDigito1 = ColorResistencia.porIndice(primerDigito);
        this.bandaDigito2 = ColorResistencia.porIndice(segundoDigito);
        this.bandaMultiplicador = ColorResistencia.porIndice(exponente);
    }

    public long getValorOhmios() {
        return valorOhmios;
    }

    public ColorResistencia getBandaDigito1() {
        return bandaDigito1;
    }

    public ColorResistencia getBandaDigito2() {
        return bandaDigito2;
    }

    public ColorResistencia getBandaMultiplicador() {
        return bandaMultiplicador;
    }

    public ColorResistencia[] obtenerBandas() {
        return new ColorResistencia[] { bandaDigito1, bandaDigito2, bandaMultiplicador };
    }

    @Override
    public String toString() {
        return valorOhmios + " ohmios -> " + bandaDigito1 + ", " + bandaDigito2 + ", " + bandaMultiplicador;
    }
}
