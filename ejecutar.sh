#!/usr/bin/env bash
# Compila todo y abre el dashboard. También puede abrir la versión de consola
# de cada proyecto.
#
#   ./ejecutar.sh               -> dashboard gráfico (los 6 proyectos)
#   ./ejecutar.sh resistencia   -> proyecto 1 en consola
#   ./ejecutar.sh carrito       -> proyecto 2 en consola
#   ./ejecutar.sh navegador     -> proyecto 3 en consola
#   ./ejecutar.sh cola          -> proyecto 4 en consola
#   ./ejecutar.sh archivos      -> proyecto 5 en consola
#   ./ejecutar.sh ruta          -> proyecto 6 en consola
#   ./ejecutar.sh capturas      -> regenera las imágenes de docs/capturas
set -e
cd "$(dirname "$0")"
rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')

case "${1:-dashboard}" in
    dashboard)   java -cp out dashboard.Dashboard ;;
    resistencia) java -cp out resistencia.Main ;;
    carrito)     java -cp out carrito.Main ;;
    navegador)   java -cp out navegador.Main --consola ;;
    cola)        java -cp out cola.Main ;;
    archivos)    java -cp out archivos.Main ;;
    ruta)        java -cp out rutamagica.RutaMagica datos/pueblos_yucatan.txt ;;
    capturas)    java -cp out dashboard.Capturas docs/capturas ;;
    *)           echo "Opción desconocida: $1"; exit 1 ;;
esac
