@echo off
rem Compila todo y abre el dashboard.
rem   ejecutar.bat               -> dashboard grafico (los 6 proyectos)
rem   ejecutar.bat carrito       -> version de consola de un proyecto
rem   (resistencia, carrito, navegador, cola, archivos, ruta)
rem Requisito: JDK 17 o superior (javac y java en el PATH).
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo No se encontro javac. Instala el JDK 17 o superior y vuelve a intentarlo.
    pause
    exit /b 1
)

if exist out rmdir /s /q out
mkdir out
rem Rutas relativas para que funcione aunque la carpeta tenga espacios.
rem Con -sourcepath, javac compila tambien todas las clases que usan.
javac -encoding UTF-8 -d out -sourcepath src ^
    src\dashboard\Dashboard.java src\dashboard\Capturas.java ^
    src\resistencia\Main.java src\resistencia\Verificacion.java ^
    src\carrito\Main.java src\navegador\Main.java src\cola\Main.java ^
    src\archivos\Main.java src\archivos\VentanaArchivos.java ^
    src\rutamagica\RutaMagica.java
if errorlevel 1 (
    echo.
    echo No se pudo compilar. Revisa la version de Java con: java -version
    pause
    exit /b 1
)

set OPCION=%1
if "%OPCION%"=="" set OPCION=dashboard
if "%OPCION%"=="dashboard"   java -cp out dashboard.Dashboard
if "%OPCION%"=="resistencia" java -cp out resistencia.Main
if "%OPCION%"=="carrito"     java -cp out carrito.Main
if "%OPCION%"=="navegador"   java -cp out navegador.Main --consola
if "%OPCION%"=="cola"        java -cp out cola.Main
if "%OPCION%"=="archivos"    java -cp out archivos.Main
if "%OPCION%"=="ruta"        java -cp out rutamagica.RutaMagica datos\pueblos_yucatan.txt
if "%OPCION%"=="capturas"    java -cp out dashboard.Capturas docs\capturas
