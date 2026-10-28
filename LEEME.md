# Zombis vs. Plantas — proyecto corregido

Incluye las fuentes, el JAR ejecutable, niveles de ejemplo, tus 34 imágenes originales, ocho variantes de estado, pruebas y los dos manuales PDF.

## Abrir el juego

Instala **Java 21 o superior**, extrae toda la carpeta y ejecuta:

- **Windows:** `ejecutar.bat`.
- **Linux/macOS:** `sh ejecutar.sh`.
- Desde una terminal en esta carpeta: `java -jar dist/ZombisVsPlantas.jar`.

Las cuentas se guardan en `cuentas`, relativa al directorio desde el que abras el programa. Conserva esa carpeta al actualizar. No se incluyen datos personales ni las cuentas creadas para las pruebas.

## Manuales

- [Manual de usuario PDF](documentacion/Manual_de_usuario.pdf): instalación y capturas de cuentas, menú, configuración, combate, pausa, guardado, reanudación y estadísticas.
- [Manual técnico PDF](documentacion/Manual_tecnico.pdf): UML, arquitectura, herencia, interfaces, algoritmos, ciclo de vida de hilos, coordenadas, IA y formatos de archivos.
- [Manual de usuario en Word](documentacion/Manual_de_usuario.docx) y [manual técnico en Word](documentacion/Manual_tecnico.docx): mismo contenido, con texto y tablas editables y las imágenes incorporadas.
- [Guía técnica breve](GUIA_TECNICA.md) y [validación](VALIDACION.md).

Las capturas de los manuales salen de la aplicación. Los historiales utilizados en el ranking son datos de demostración aislados; la escena comparativa de zombis es una escena de prueba del renderizador.

## Compilar

Necesitas el **JDK 21** o superior, con `javac`, `java` y `jar` en PATH.

- Windows: `compilar.bat`.
- Linux/macOS: `sh compilar.sh`.

El resultado es `dist/ZombisVsPlantas.jar`, con recursos incluidos y Main-Class configurada. La aplicación solo requiere el JDK estándar: sin Maven/Gradle, AbsoluteLayout ni otras bibliotecas externas. Los PDF ya generados tampoco requieren instalar herramientas adicionales para usar el juego.

Las fuentes están organizadas por paquetes en `src/main/java`. Para abrirlas en un IDE, selecciona esa raíz de fuentes y la clase principal `com.mycompany.proyecto2_plantasvszombies.principal.Proyecto2_PlantasVsZombies`. Los formularios antiguos no deben reemplazar las pantallas corregidas: reintroducirían eventos y dependencias anteriores.

## Correcciones incluidas

- Lector propio de JSON con validación de filas, columnas, identificadores y parámetros.
- Cerebros recolectables, botín de plantas, hordas, podadoras y resumen final.
- Tres dificultades con defensa automática en orden de prioridad.
- Un hilo de reloj y **6/5/4 hilos generadores independientes**. Todos entregan sus acciones al EDT de Swing; al pausar se interrumpen y al reanudar se recrean conservando las fechas pendientes.
- Guardado binario completo y carga en pausa, sin serializar hilos ni ventanas.
- Estadísticas y tres rankings Top 5; zombis comprados y generados se cuentan por separado.
- Común sin brazo, Deportista sin casco, Garrocha sin arma y con/sin brazo, Zombistein sin Zombidito después de lanzarlo, Bailarín y Zombidito con sprite propio. El lanzamiento tiene trayectoria aérea.
- Imágenes originales conservadas y empaquetadas. Los botones del menú/nueva partida miden 170 × 40 y el selector 120 × 28; no se estiran al ampliar la ventana.

## Decisiones del juego

Los costos fraccionarios se redondean hacia arriba: un común cuesta 13 en la horda final y Zombistein 88. El catálogo y el cobro usan la misma regla.

El enunciado menciona soles de la IA pero no fija saldo, generación ni costos. La defensa decide por prioridad y casillas disponibles, sin inventar una economía de soles.

Las carpetas de cuentas nuevas codifican el nickname en hexadecimal para distinguir mayúsculas incluso en Windows. Se conserva la lectura de carpetas anteriores.

Las variantes gráficas se agregan como archivos nuevos; tus 34 originales mantienen su contenido. Los estados no dependen de editar datos de vida al dibujar.

## Pruebas

`sh probar.sh` compila y ejecuta 52 comprobaciones de lógica, 32 de concurrencia y 22 visuales/lanzamiento, en un directorio temporal. Para agregar pruebas de interfaz y una victoria real por clics, en una sesión gráfica:

```sh
PROBAR_INTERFAZ=1 sh probar.sh
```

Se incluye `herramientas/CapturarManuales.java` para reproducir las capturas usando una carpeta de trabajo temporal. No lo ejecutes sobre tus cuentas personales: crea historiales de demostración.

`herramientas/generar_manuales.py` usa Python, ReportLab y Pillow para regenerar los PDF. Los diagramas DOT se renderizan con Graphviz. Estas son herramientas de documentación, no dependencias Java del juego.

Para regenerar los archivos Word: `python3 herramientas/generar_word.py`. Requiere python-docx, ReportLab y Pillow solo como herramientas de documentación; no modifica los PDF existentes ni agrega dependencias al juego.
