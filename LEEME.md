# Zombis vs. Plantas — versión corregida

Esta copia parte de las clases Java que enviaste. Los originales y el repositorio anterior no se modificaron.

## Ejecutar

Necesitas Java 21 o superior y una sesión gráfica de escritorio.

Desde la carpeta del proyecto:

```text
java -jar dist/ZombisVsPlantas.jar
```

Crea o selecciona una cuenta. En «Nueva partida» elige un archivo de `Niveles` y una dificultad. Selecciona un zombi del catálogo y pulsa un carril. Pulsa los cerebros rosados para recogerlos. Cada cerebro entrega 25 unidades y desaparece a los 5 segundos de juego.

El botón «Pausar / Reanudar» detiene el juego y abre las opciones de reanudar o guardar y salir. Para recuperar una partida utiliza «Reanudar partida guardada» desde el menú: el tablero se reconstruye pausado.

Las cuentas se guardan en `cuentas`, relativa al directorio desde el que ejecutes Java. Ejecuta siempre desde la misma carpeta para ver las mismas cuentas. Los nombres de las carpetas nuevas se codifican en hexadecimal para distinguir nicknames como `Ana` y `ana`, incluso en Windows. Las carpetas de cuentas antiguas también se leen.

## Compilar sin bibliotecas externas

Instala el **JDK 21** o superior y asegúrate de que `java`, `javac` y `jar` estén en PATH.

- Windows: ejecuta `compilar.bat` desde esta carpeta.
- Linux/macOS: ejecuta `sh compilar.sh`.

El resultado es `dist/ZombisVsPlantas.jar`. No requiere Maven, Gradle ni AbsoluteLayout. Las fuentes están organizadas por paquetes en `src/main/java`, y los recursos en `src/main/resources`.

Las pantallas de nueva partida, menú y juego se construyen con Swing estándar. No reemplaces esas clases por los `.form` viejos: volverían a introducir diseños y eventos anteriores. Para abrirlo en NetBeans puedes crear un proyecto Java con fuentes existentes y seleccionar `src/main/java` como raíz, con la clase principal `com.mycompany.proyecto2_plantasvszombies.principal.Proyecto2_PlantasVsZombies`.

## Cambios

- Lector JSON que respeta filas, espacios y validaciones; rechaza carriles irregulares, identificadores desconocidos, jardines vacíos y parámetros inválidos.
- Compilación solo con JDK; imágenes cargadas desde recursos del JAR, sin rutas de Windows.
- Caracono pierde el escudo antes de recibir daño en salud y pasa a daño 15 sin cono.
- Cerebros con procesos lógicos independientes (6/5/4 según dificultad), espera aleatoria 5–15 segundos, duración 5 segundos y botín de 3–6 al eliminar una planta.
- Cerebros dibujados y detectados sobre las demás entidades; recoger no invoca zombis ni bloquea la simulación.
- Defensa automática por dificultad, con una acción por evaluación y prioridades en orden.
- Hordas en los bloques 4 y 7, con precios y aviso visibles. Las fronteras usan fracciones de segundo.
- Guardado binario de modelo, motor, proyectiles, efectos, cerebros, generadores, IA, podadoras y fracción del reloj; carga en pausa.
- Estadísticas al finalizar una partida, tres rankings Top 5 y resumen de cuenta.
- Contadores separados para zombis comprados y zombis generados gratuitamente.
- Estados visuales de plantas y zombis, podadoras y aplastamiento. Se usan los sprites originales cuando estan disponibles; los dibujos vectoriales quedan como respaldo para archivos faltantes.

## Decisiones que el enunciado deja abiertas

- Los costos fraccionarios se redondean hacia arriba: en horda final un común cuesta 13 y Zombistein 88. El catálogo y el cobro usan la misma regla.
- El enunciado menciona soles de la IA, pero no define saldo inicial, generación ni costos de plantas. No se inventó una economía de soles; la defensa elige según prioridad y casillas disponibles.
- Los generadores son procesos lógicos con estado independiente, ejecutados por el mismo reloj del juego. No son 4/5/6 hilos de sistema operativo. Esto mantiene todo el estado mutable en el hilo de Swing, sin sincronización prohibida.
- Cuando faltan sprites específicos, las plantas tienen marcadores y los zombis dibujos vectoriales. Se incluyeron las 34 imágenes de tu ZIP, incluidos jardines, plantas y los seis zombis del catálogo.
- El guardado utiliza serialización Java con versión y filtro de clases. Es compatible con esta versión corregida, no con formatos de otras implementaciones.

## Pruebas

`sh probar.sh` compila el JAR y ejecuta las pruebas de lógica en un directorio temporal. Para probar la interfaz en una sesión gráfica:

```sh
PROBAR_INTERFAZ=1 sh probar.sh
```

Las pruebas comprueban JSON, escudos, descuentos, pausa, podadoras, victoria, todas las prioridades de la IA, botín, economía, explosiones, salto bloqueado, cuentas y continuidad después de guardar. La prueba gráfica comprueba el hilo, la pausa visible, el pintado, el guardado y el regreso al menú.

Consulta `GUIA_TECNICA.md` para entender las clases y algoritmos. Esta guía no sustituye los manuales PDF con UML y capturas solicitados para la entrega académica.

## Restauración de imágenes

Se restauraron los fondos disponibles de menú y nueva partida. El tablero y el catálogo usan los sprites originales cuando están presentes. Además del contenido del JAR, se buscan imágenes en la carpeta `Imagenes` junto al directorio de ejecución y en `src/main/resources/Imagenes`.

Nombres esperados: `zombi.png`, `caracono.png`, `garrocha.png`, `deportista.png`, `bailon.png`, `zombistein.png`, `bailarin.png`, `zombidito.png`, `lanzaguisantes.png`, `hielaguisantes.png`, `patatapum.png`, `nuez.png`, `carronivora.png`, `pinchohierba.png`, `bipetidora.png`. El tablero también admite JPG y GIF con el mismo nombre base.

Para variantes de estado se admiten `zombi_sin_brazo.png`, `caracono_sin_cono.png`, `deportista_sin_casco.png` y `garrocha_sin_garrocha.png`. Si no existe una variante se indica el estado con texto o se conserva el respaldo. Los cambios en las imágenes no cambian las reglas de combate.

Las 34 imágenes de Imagenes.zip están incluidas sin modificar sus archivos. Se usan los jardines y las plantas originales (priorizando las versiones _sin_fondo), y los seis sprites del catálogo tanto en los botones como en el tablero. El ZIP no contiene sprites separados de Bailarín, Zombidito o variantes de daño; esas variantes conservan los respaldos documentados.

Vista comprobada: `capturas/partida-con-tus-imagenes.png` (partida pausada; los botones se muestran deshabilitados por la pausa).
