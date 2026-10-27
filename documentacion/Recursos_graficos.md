# Recursos gráficos

Se conservaron, byte a byte, las 34 imágenes recibidas en Imagenes.zip. El JAR contiene las mismas copias de esos originales.

Se agregaron ocho variantes PNG transparentes mediante una herramienta de edición de imágenes, usando como referencia los sprites originales:

| Archivo nuevo | Uso |
|---|---|
| zombi_sin_brazo.png | Común al 50% de vida y Caracono sin cono con salud baja. |
| deportista_sin_casco.png | Deportista con escudo agotado. |
| garrocha_sin_garrocha.png | Saltador después de gastar el salto. |
| garrocha_con_garrocha_sin_brazo.png | Saltador dañado que todavía conserva la garrocha. |
| garrocha_sin_brazo.png | Saltador dañado después de gastar el salto. |
| zombistein_sin_zombidito.png | Zombistein después del lanzamiento. |
| bailarin.png | Bailarín generado por Bailón, con traje púrpura. |
| zombidito.png | Pequeño zombi lanzado por Zombistein. |

La selección del sprite depende del estado del modelo. Los sprites no cambian salud, costo ni reglas. El vuelo del Zombidito se anima en el panel del juego a partir de las coordenadas y fechas guardadas, y se detiene durante la pausa.
