#!/bin/sh
set -eu
cd "$(dirname "$0")"
mkdir -p build/classes dist
find src/main/java -name '*.java' > build/fuentes.txt
javac --release 21 -encoding UTF-8 -d build/classes @build/fuentes.txt
jar --create --file dist/ZombisVsPlantas.jar --main-class com.mycompany.proyecto2_plantasvszombies.principal.Proyecto2_PlantasVsZombies -C build/classes . -C src/main/resources .
echo 'Compilado: dist/ZombisVsPlantas.jar'
