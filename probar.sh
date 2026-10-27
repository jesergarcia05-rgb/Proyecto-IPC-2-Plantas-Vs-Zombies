#!/bin/sh
set -eu
cd "$(dirname "$0")"
sh compilar.sh
mkdir -p build/tests
javac --release 21 -encoding UTF-8 -cp build/classes -d build/tests tests/Pruebas.java tests/PruebasInterfaz.java tests/PruebasHilos.java tests/PruebasVisuales.java tests/PruebasPartidaCompleta.java
raiz=$(pwd)
pruebas=$(mktemp -d)
trap 'rm -rf "$pruebas"' EXIT HUP INT TERM
cd "$pruebas"
java -ea -cp "$raiz/build/classes:$raiz/build/tests" Pruebas
java -ea -Djava.awt.headless=true -cp "$raiz/build/classes:$raiz/build/tests" PruebasHilos
java -ea -Djava.awt.headless=true -cp "$raiz/dist/ZombisVsPlantas.jar:$raiz/build/tests" PruebasVisuales
if [ "${PROBAR_INTERFAZ:-0}" = 1 ]; then
 java -ea -cp "$raiz/dist/ZombisVsPlantas.jar:$raiz/build/tests" PruebasInterfaz
 java -ea -cp "$raiz/dist/ZombisVsPlantas.jar:$raiz/build/tests" PruebasPartidaCompleta
fi
