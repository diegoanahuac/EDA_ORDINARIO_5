package resistencia.modelo;

/**
 * Circuito en paralelo: Rtp = 1 / (1/R1 + 1/R2 + ... + 1/RN)
 */
public class CircuitoParalelo extends CircuitoResistencias {

    public CircuitoParalelo(int n) {
        super(n);
    }

    @Override
    public double calcularTotal() {
        double sumaInversos = 0;
        for (Resistencia r : getResistencias()) {
            sumaInversos += 1.0 / r.getValorOhmios();
        }
        return sumaInversos == 0 ? 0 : 1.0 / sumaInversos;
    }

    @Override
    public String getTipo() {
        return "Paralelo";
    }
}
