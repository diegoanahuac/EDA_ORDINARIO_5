package dashboard.clase;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

/**
 * Lee el código fuente real de un método desde la carpeta {@code src/} para
 * mostrarlo tal cual en el dashboard. Así lo que se ve en pantalla siempre
 * coincide con el programa que se ejecuta.
 */
public final class ExtractorCodigo {

    /** Fragmento de código con la línea donde empieza en su archivo. */
    public static final class Fragmento {
        private final String codigo;
        private final int lineaInicial;
        private final int lineaFinal;
        private final boolean encontrado;

        Fragmento(String codigo, int lineaInicial, int lineaFinal, boolean encontrado) {
            this.codigo = codigo;
            this.lineaInicial = lineaInicial;
            this.lineaFinal = lineaFinal;
            this.encontrado = encontrado;
        }

        public String getCodigo() {
            return codigo;
        }

        public int getLineaInicial() {
            return lineaInicial;
        }

        public int getLineaFinal() {
            return lineaFinal;
        }

        public boolean isEncontrado() {
            return encontrado;
        }
    }

    private ExtractorCodigo() {
    }

    /**
     * Busca la carpeta {@code src/}: primero en el directorio de trabajo
     * (cuando se usa ejecutar.sh) y luego junto a la carpeta de clases.
     */
    static File carpetaFuentes() {
        File local = new File("src");
        if (local.isDirectory()) {
            return local;
        }
        try {
            File clases = new File(ExtractorCodigo.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            File junto = new File(clases.getParentFile(), "src");
            if (junto.isDirectory()) {
                return junto;
            }
        } catch (Exception e) {
            // Se usa la ruta relativa.
        }
        return local;
    }

    /**
     * Extrae un método completo (declaración y cuerpo) a partir del inicio de
     * su firma, contando llaves para saber dónde termina.
     *
     * @param archivo ruta relativa a {@code src/}
     * @param firma texto con el que empieza la declaración
     * @return el fragmento, o un aviso si no se encontró
     */
    public static Fragmento extraer(String archivo, String firma) {
        File fuente = new File(carpetaFuentes(), archivo);
        List<String> lineas;
        try {
            lineas = Files.readAllLines(fuente.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return new Fragmento("// No se pudo leer " + archivo
                    + "\n// Ejecuta el dashboard desde la carpeta del proyecto (./ejecutar.sh).", 0, 0, false);
        }

        int inicio = -1;
        for (int i = 0; i < lineas.size(); i++) {
            if (lineas.get(i).trim().startsWith(firma)) {
                inicio = i;
                break;
            }
        }
        if (inicio < 0) {
            return new Fragmento("// No se encontró \"" + firma + "\" en " + archivo, 0, 0, false);
        }

        int profundidad = 0;
        boolean abrio = false;
        int fin = inicio;
        for (int i = inicio; i < lineas.size(); i++) {
            String linea = sinCadenasNiComentarios(lineas.get(i));
            for (char c : linea.toCharArray()) {
                if (c == '{') {
                    profundidad++;
                    abrio = true;
                } else if (c == '}') {
                    profundidad--;
                }
            }
            // Métodos abstractos o de interfaz terminan en ';' sin cuerpo.
            if (!abrio && linea.trim().endsWith(";")) {
                fin = i;
                break;
            }
            if (abrio && profundidad == 0) {
                fin = i;
                break;
            }
        }

        int sangria = sangria(lineas.get(inicio));
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i <= fin; i++) {
            String linea = lineas.get(i);
            sb.append(linea.length() >= sangria && linea.substring(0, sangria).isBlank()
                    ? linea.substring(sangria) : linea.trim());
            if (i < fin) {
                sb.append('\n');
            }
        }
        return new Fragmento(sb.toString(), inicio + 1, fin + 1, true);
    }

    private static int sangria(String linea) {
        int n = 0;
        while (n < linea.length() && linea.charAt(n) == ' ') {
            n++;
        }
        return n;
    }

    /** Quita cadenas, caracteres y comentarios de línea para contar llaves sin errores. */
    private static String sinCadenasNiComentarios(String linea) {
        StringBuilder sb = new StringBuilder();
        boolean cadena = false;
        boolean caracter = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (!cadena && !caracter && c == '/' && i + 1 < linea.length() && linea.charAt(i + 1) == '/') {
                break;
            }
            if (c == '\\' && (cadena || caracter)) {
                i++;
                continue;
            }
            if (c == '"' && !caracter) {
                cadena = !cadena;
                continue;
            }
            if (c == '\'' && !cadena) {
                caracter = !caracter;
                continue;
            }
            if (!cadena && !caracter) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
