package resistencia.modelo;

/**
 * Circuito en serie: Rts = R1 + R2 + ... + RN
 */
public class CircuitoSerie extends CircuitoResistencias {

    public CircuitoSerie(int n) {
        super(n);
    }

    @Override
    public double calcularTotal() {
        double total = 0;
        for (Resistencia r : getResistencias()) {
            total += r.getValorOhmios();
        }
        return total;
    }

    @Override
    public String getTipo() {
        return "Serie";
    }
}
