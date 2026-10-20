# Validación realizada

- Compilación completa con JDK 21, sin classpath de terceros: correcta.
- Creación del JAR con Main-Class y recursos incluidos: correcta.
- 52 comprobaciones de lógica y persistencia: correctas.
- Pruebas Swing sobre pantalla virtual: correctas. Se comprobó carga pausada, reanudación, avance del hilo, compra por clic y cobro, recolección por clic sin invocar accidentalmente, pausa, pintado, guardado binario y regreso al menú.
- El recorrido de Swing se ejecutó cargando las clases de la aplicación desde el JAR generado.
- Revisión de fuentes: sin AbsoluteLayout, Collections dinámicas, Timer de Swing, volatile, synchronized, java.util.concurrent ni rutas absolutas de Windows.
- Repositorio original: sin cambios.

Las pruebas se ejecutaron en Linux con Java 21 y Xvfb. No se ejecutaron en una máquina Windows; se incluyen scripts de compilación y ejecución para ese sistema. Se incorporaron las 34 imágenes originales de Imagenes.zip. Se verificó que cada archivo empaquetado en el JAR es idéntico al original y se inspeccionó una captura de partida mostrando sprites, jardín y catálogo. Solo los recursos o variantes que no existen en el ZIP usan respaldos. Los manuales PDF académicos no forman parte de esta corrección de código; LEEME.md y GUIA_TECNICA.md documentan la implementación entregada.

Ajuste del menú: botones de 170 × 40, como en el diseño original. Se comprobó su tamaño y que no se estiran al ampliar la ventana. El JAR se recompiló y la captura está en capturas/menu-botones-pequenos.png.

Ajuste de Nueva partida: tres botones de 170 × 40 y selector de dificultad de 120 × 28, centrados y sin estirarse al ampliar la ventana. Compilación y comprobación visual correctas; captura en capturas/nueva-partida-botones-pequenos.png.
