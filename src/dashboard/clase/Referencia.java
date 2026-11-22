package dashboard.clase;

/**
 * Relaciona una función del proyecto con un tema visto en clase.
 * Guarda dónde está el código (archivo y método) y de qué material de
 * Brightspace sale el tema, para mostrarlos juntos en la pestaña
 * "Código y clase".
 */
public final class Referencia {
    private final String concepto;
    private final String parcial;
    private final String tema;
    private final String material;
    private final String archivo;
    private final String firma;
    private final String explicacion;

    /**
     * @param concepto qué se muestra (por ejemplo "Insertar en un ABB")
     * @param parcial "Parcial 1" o "Parcial 2"
     * @param tema módulo de Brightspace (Contenido) donde se vio
     * @param material archivo o recurso de ese módulo
     * @param archivo ruta del código dentro de {@code src/}
     * @param firma inicio de la declaración del método, para encontrarlo
     * @param explicacion cómo se relaciona el código con lo visto en clase
     */
    public Referencia(String concepto, String parcial, String tema, String material,
                      String archivo, String firma, String explicacion) {
        this.concepto = concepto;
        this.parcial = parcial;
        this.tema = tema;
        this.material = material;
        this.archivo = archivo;
        this.firma = firma;
        this.explicacion = explicacion;
    }

    public String getConcepto() {
        return concepto;
    }

    public String getParcial() {
        return parcial;
    }

    public String getTema() {
        return tema;
    }

    public String getMaterial() {
        return material;
    }

    public String getArchivo() {
        return archivo;
    }

    public String getFirma() {
        return firma;
    }

    public String getExplicacion() {
        return explicacion;
    }

    @Override
    public String toString() {
        return concepto;
    }
}
