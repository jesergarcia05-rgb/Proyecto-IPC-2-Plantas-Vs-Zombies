@echo off
setlocal
cd /d "%~dp0"
if not exist build\classes mkdir build\classes
if not exist dist mkdir dist
(for /r src\main\java %%f in (*.java) do @echo "%%f") > build\fuentes.txt
javac --release 21 -encoding UTF-8 -d build\classes @build\fuentes.txt
if errorlevel 1 exit /b 1
jar --create --file dist\ZombisVsPlantas.jar --main-class com.mycompany.proyecto2_plantasvszombies.principal.Proyecto2_PlantasVsZombies -C build\classes . -C src\main\resources .
if errorlevel 1 exit /b 1
echo Compilado: dist\ZombisVsPlantas.jar
