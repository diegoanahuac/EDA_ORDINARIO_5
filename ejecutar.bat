@echo off
rem Compila todo y abre el dashboard.
rem   ejecutar.bat               -> dashboard grafico (los 6 proyectos)
rem   ejecutar.bat carrito       -> version de consola de un proyecto
rem   (resistencia, carrito, navegador, cola, archivos, ruta)
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
del fuentes.txt
if errorlevel 1 exit /b 1

set OPCION=%1
if "%OPCION%"=="" set OPCION=dashboard
if "%OPCION%"=="dashboard"   java -cp out dashboard.Dashboard
if "%OPCION%"=="resistencia" java -cp out resistencia.Main
if "%OPCION%"=="carrito"     java -cp out carrito.Main
if "%OPCION%"=="navegador"   java -cp out navegador.Main --consola
if "%OPCION%"=="cola"        java -cp out cola.Main
if "%OPCION%"=="archivos"    java -cp out archivos.Main
if "%OPCION%"=="ruta"        java -cp out rutamagica.RutaMagica datos\pueblos_yucatan.txt
