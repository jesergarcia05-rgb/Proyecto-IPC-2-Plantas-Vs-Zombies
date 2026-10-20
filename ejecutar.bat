@echo off
cd /d "%~dp0"
java -jar dist\ZombisVsPlantas.jar
if errorlevel 1 pause
