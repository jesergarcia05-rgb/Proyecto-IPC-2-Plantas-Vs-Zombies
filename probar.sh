#!/bin/sh
set -eu
cd "$(dirname "$0")"
sh compilar.sh
mkdir -p build/tests
javac --release 21 -encoding UTF-8 -cp build/classes -d build/tests tests/Pruebas.java tests/PruebasInterfaz.java
raiz=$(pwd)
pruebas=$(mktemp -d)
trap 'rm -rf "$pruebas"' EXIT HUP INT TERM
cd "$pruebas"
java -ea -cp "$raiz/build/classes:$raiz/build/tests" Pruebas
if [ "${PROBAR_INTERFAZ:-0}" = 1 ]; then
 java -ea -cp "$raiz/dist/ZombisVsPlantas.jar:$raiz/build/tests" PruebasInterfaz
fi
