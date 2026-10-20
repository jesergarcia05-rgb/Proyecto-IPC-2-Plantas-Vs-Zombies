#!/bin/sh
set -eu
cd "$(dirname "$0")"
exec java -jar dist/ZombisVsPlantas.jar
