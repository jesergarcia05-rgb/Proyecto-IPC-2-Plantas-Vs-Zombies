# Validación de la entrega actual

- Compilación con JDK 21 estándar, sin classpath de terceros: correcta.
- JAR ejecutable con Main-Class y recursos empaquetados: correcto.
- **52 comprobaciones de lógica y persistencia:** correctas.
- **32 comprobaciones de concurrencia:** correctas. Verifican 6/5/4 hilos reales, emisión por trabajador, detención en pausa, carga sin duplicados y ausencia de trabajadores huérfanos.
- **22 comprobaciones visuales y de lanzamiento:** correctas. Verifican selección de variantes de estado, vuelo, pausa y aterrizaje.
- **Prueba de interfaz:** compra y recolección por clic, pausa, pintado, guardado y regreso al menú, correctos.
- **Partida completa por clics y reloj real:** primera llegada consume podadora; segunda produce victoria; resumen y estadísticas persistidos, correctos.
- **Flujo completo de documentación:** crear/seleccionar cuenta, explorador JSON, configuración, partida, generación de cerebros por hilos, guardar, cargar exactamente en pausa, derrota en un nivel corto, estadísticas y rankings, correctos.
- **Compatibilidad:** un binario creado con el JAR anterior se cargó y actualizó con esta versión.
- Las 34 imágenes originales se conservaron byte a byte. Se agregaron ocho variantes transparentes de estado, sin sustituir esos originales.
- Botones de menú/nueva partida de 170 × 40 y selector de 120 × 28, conservados.
- Manual técnico y manual de usuario PDF generados, con diagramas UML y capturas incluidas.

La aplicación no utiliza bibliotecas Java externas, Collections dinámicas, Lombok, Timer de Swing, volatile, synchronized, wait/notify ni java.util.concurrent. Todos los campos de las clases de aplicación son privados o protegidos. El código de herramientas y pruebas está separado de src/main/java.

Las pruebas se ejecutaron en Linux con JDK 21 y pantalla virtual Xvfb. Los scripts de Windows están incluidos, pero no se probaron en una máquina Windows. Las comprobaciones cubren los casos descritos y no sustituyen la revisión académica de todos los posibles niveles.

Las capturas usan cuentas/datos de demostración en un directorio temporal; esos historiales no se entregan como cuentas reales. La escena de estados visuales es una escena de prueba identificada como tal en los manuales.

## Versiones Word

Se generaron Manual_de_usuario.docx y Manual_tecnico.docx con el mismo contenido fuente de los PDF. Se comprobó la totalidad del texto, las 18 imágenes y 2 tablas del manual de usuario, y las 6 imágenes y 5 tablas del técnico. Los contenedores y XML se validaron y se abrió cada DOCX con LibreOffice para revisar su distribución (17 y 18 páginas respectivamente, sin páginas vacías). Se conservaron los PDF originales.
