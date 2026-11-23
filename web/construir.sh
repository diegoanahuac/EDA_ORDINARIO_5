#!/usr/bin/env bash
# Arma la versión web (JAR con clases y fuentes, página y capturas) en la carpeta indicada.
#   web/construir.sh salida/
set -e
salida="$(mkdir -p "$1" && cd "$1" && pwd)"
cd "$(dirname "$0")/.."
tmp="$(mktemp -d)"
javac --release 11 -encoding UTF-8 -d "$tmp" $(find src -name '*.java')
cp -R src "$tmp/src"
jar --create --file "$salida/EDA_Ordinario.jar" --main-class dashboard.Web -C "$tmp" .
mkdir -p "$salida/capturas"
cp docs/capturas/clase_*.png "$salida/capturas/"
cp web/index.html "$salida/"
touch "$salida/.nojekyll"
rm -rf "$tmp"
