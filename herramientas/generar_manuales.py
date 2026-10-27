#!/usr/bin/env python3
"""Genera los PDF de documentación. Requiere Python/reportlab solo para regenerar PDF,
no para compilar o ejecutar el juego Java. Capturas producidas por CapturarManuales."""
from pathlib import Path
from xml.sax.saxutils import escape
from reportlab.platypus import (SimpleDocTemplate, Paragraph, Spacer, PageBreak, Image,
                              Table, TableStyle, Preformatted, KeepTogether)
from reportlab.lib import colors
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from PIL import Image as PILImage

RAIZ = Path(__file__).resolve().parents[1]
DOC = RAIZ / 'documentacion'
CAP = DOC / 'capturas'
for nombre, archivo in [('Texto','DejaVuSans.ttf'),('Negrita','DejaVuSans-Bold.ttf'),('Mono','DejaVuSansMono.ttf')]:
    pdfmetrics.registerFont(TTFont(nombre, '/usr/share/fonts/truetype/dejavu/' + archivo))
pdfmetrics.registerFontFamily('Texto', normal='Texto', bold='Negrita', italic='Texto', boldItalic='Negrita')
VERDE = colors.HexColor('#205742')
GRIS = colors.HexColor('#50605B')
ANCHO = A4[0] - 88
estilos = getSampleStyleSheet()
estilos.add(ParagraphStyle(name='Cuerpo', fontName='Texto', fontSize=10, leading=14, spaceAfter=9))
estilos.add(ParagraphStyle(name='Titulo', fontName='Negrita', fontSize=28, leading=34, textColor=VERDE, spaceAfter=18))
estilos.add(ParagraphStyle(name='Seccion', fontName='Negrita', fontSize=16, leading=21, textColor=VERDE, spaceBefore=5, spaceAfter=14))
estilos.add(ParagraphStyle(name='Subtitulo', fontName='Negrita', fontSize=11.5, leading=16, textColor=VERDE, spaceBefore=12, spaceAfter=8))
estilos.add(ParagraphStyle(name='Pie', fontName='Texto', fontSize=8, leading=11, textColor=GRIS, spaceAfter=10))
estilos.add(ParagraphStyle(name='Celda', fontName='Texto', fontSize=8.3, leading=11, spaceAfter=0))
estilos.add(ParagraphStyle(name='Codigo', fontName='Mono', fontSize=8, leading=11, spaceAfter=10))

def p(texto, estilo='Cuerpo'):
    return Paragraph(texto, estilos[estilo])

def imagen(nombre, ancho=ANCHO, alto=380, carpeta=CAP):
    ruta = carpeta / nombre
    w, h = PILImage.open(ruta).size
    factor = min(ancho / w, alto / h)
    return Image(str(ruta), width=w * factor, height=h * factor)

def tabla(filas, anchos=None):
    celdas = [[p(escape(str(c)), 'Celda') for c in fila] for fila in filas]
    t = Table(celdas, colWidths=anchos, hAlign='LEFT', repeatRows=1)
    t.setStyle(TableStyle([
        ('BACKGROUND',(0,0),(-1,0),colors.HexColor('#DCECE4')),
        ('VALIGN',(0,0),(-1,-1),'TOP'),('GRID',(0,0),(-1,-1),.4,colors.HexColor('#BCCFC5')),
        ('LEFTPADDING',(0,0),(-1,-1),7),('RIGHTPADDING',(0,0),(-1,-1),7),
        ('TOPPADDING',(0,0),(-1,-1),6),('BOTTOMPADDING',(0,0),(-1,-1),6),
    ]))
    return t

def paginas(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(colors.HexColor('#BCCFC5'));canvas.line(44,43,A4[0]-44,43)
    canvas.setFillColor(GRIS);canvas.setFont('Texto',8)
    canvas.drawString(44,30,'Zombis vs. Plantas · Proyecto 2 · IPC1 · 2S2026')
    canvas.drawRightString(A4[0]-44,30,str(doc.page))
    canvas.restoreState()

def guardar(nombre, contenido, titulo):
    SimpleDocTemplate(str(DOC/nombre), pagesize=A4, rightMargin=44, leftMargin=44,
        topMargin=42, bottomMargin=56, title=titulo, author='Proyecto Zombis vs. Plantas',
        pageCompression=1).build(contenido, onFirstPage=paginas, onLaterPages=paginas)

def portada(titulo, subtitulo):
    return [Spacer(1,36),p('Zombis vs. Plantas','Titulo'),p(titulo,'Seccion'),p(subtitulo),
        p('Introducción a la Programación y Computación 1<br/>Centro Universitario de Occidente · USAC<br/>Segundo semestre 2026'),
        Spacer(1,16),imagen('07-partida.png',alto=290),Spacer(1,15),
        p('Versión de entrega con imágenes originales, controles compactos, estados gráficos y generadores con hilos independientes.'),
        p('Repositorio: jesergarcia05-rgb/Proyecto-IPC-2-Plantas-Vs-Zombies<br/>Rama: entrega-corregida-con-imagenes','Pie'),PageBreak()]

def capitulo(s, titulo, texto, foto=None, pie=None, alto=380):
    s.append(p(titulo,'Seccion'))
    for linea in texto:s.append(p(linea))
    if foto:
        s += [Spacer(1,5), imagen(foto,alto=alto),Spacer(1,7)]
        if pie:s.append(p(pie,'Pie'))

usuario = portada('Manual de usuario','Guía de instalación y uso, con capturas del flujo real de la aplicación.')
capitulo(usuario,'1. Instalar y abrir el juego',[
    'Necesitas <b>Java 21 o superior</b> y una sesión gráfica de escritorio. Descarga el ZIP de la rama indicada y extrae toda la carpeta antes de ejecutarlo.',
    '<b>Windows:</b> abre <b>ejecutar.bat</b>. <b>Linux/macOS:</b> ejecuta <b>sh ejecutar.sh</b> desde la carpeta del proyecto. También puedes usar <b>java -jar dist/ZombisVsPlantas.jar</b>.',
    'En la pantalla inicial elige <b>Crear Cuenta</b> o <b>Seleccionar Cuenta</b>. Guarda siempre tus datos ejecutando el programa desde la misma carpeta; las cuentas se almacenan en la subcarpeta <b>cuentas</b>.'
], '01-inicio.png','Figura 1. Pantalla inicial. Se conservan los fondos y controles del proyecto.',alto=325)
usuario.append(p('Si aparece “java no se reconoce”, instala Java y comprueba <b>java -version</b>. No abras el JAR con un descompresor.'))
usuario.append(PageBreak())
capitulo(usuario,'2. Crear una cuenta',[
    '1. Pulsa <b>Crear Cuenta</b> y escribe un nickname de <b>1 a 12 caracteres alfanuméricos</b>. No uses espacios internos ni símbolos.',
    '2. Pulsa <b>Crear</b>. El nickname debe ser único: <b>Ana</b> y <b>ana</b> son cuentas distintas. Al confirmar se crean las carpetas y el archivo de estadísticas, y se abre el menú.',
    '3. Si aparece un error, revisa longitud, caracteres y si el nombre ya existe. <b>Cancelar</b> vuelve al inicio.'
], '02-crear-cuenta.png','Figura 2. Captura con la cuenta de demostración DemoIPC1. Los datos de ejemplo no se incluyen como cuentas personales en la entrega.',alto=350)
usuario.append(PageBreak())
capitulo(usuario,'3. Seleccionar una cuenta existente',[
    'Al abrir el programa, elige <b>Seleccionar Cuenta</b>. Despliega la lista, selecciona tu nickname y pulsa <b>Continuar</b>. La información de esa cuenta se carga automáticamente.',
    'Si la lista está vacía, vuelve con <b>Cancelar</b> y crea una cuenta. Para cambiar de jugador, cierra el programa y vuelve a abrirlo.'
], '03-seleccionar-cuenta.png','Figura 3. Selector de cuentas existentes.',alto=455)
usuario.append(PageBreak())
capitulo(usuario,'4. Usar el menú principal',[
    '<b>Nueva partida:</b> elige un nivel JSON y la dificultad. <b>Reanudar partida:</b> abre tus archivos guardados. <b>Estadísticas:</b> muestra tu historial y los rankings. <b>Salir:</b> cierra el menú.',
    'El nombre de la cuenta activa aparece arriba. Los botones conservan tamaño compacto y no se estiran al ampliar la ventana.'
], '04-menu.png','Figura 4. Menú principal con botones de 170 × 40 unidades de Swing.',alto=360)
usuario.append(PageBreak())
capitulo(usuario,'5. Elegir un nivel y la dificultad',[
    '1. Pulsa <b>Elegir nivel JSON</b>. Busca un archivo de la carpeta <b>Niveles</b>, por ejemplo <b>nivel1_5x9.json</b>, y confirma con el botón del explorador.',
    '2. Comprueba el nombre mostrado. Selecciona <b>Fácil</b>, <b>Medio</b> o <b>Difícil</b>. Pulsa <b>Iniciar</b>. <b>Volver al menú</b> cancela la configuración.',
    'El archivo define duración, cerebros iniciales y plantas del jardín. Si es inválido se muestra el motivo y no empieza la partida.'
], '06-configuracion.png','Figura 5. Configuración con selector pequeño de dificultad y archivo seleccionado.',alto=310)
usuario += [p('El explorador de archivos','Subtitulo'),imagen('05-explorador.png',ancho=300,alto=210),p('Figura 6. Selección del archivo JSON en el explorador del sistema.','Pie'),PageBreak()]
capitulo(usuario,'6. Invocar zombis y controlar la partida',[
    'Los zombis entran por la <b>izquierda</b> y avanzan hacia la casa de la <b>derecha</b>. Arriba se muestran tus cerebros, el tiempo restante, la dificultad, el bloque y el botón de pausa.',
    '1. Pulsa un tipo de zombi del catálogo inferior. 2. Pulsa un carril del jardín. Se descuenta el precio y el zombi aparece al inicio de ese carril. Pulsar de nuevo el mismo tipo cancela la selección.',
    'Si no tienes cerebros suficientes, se muestra un mensaje y la compra no se realiza. Los precios visibles se actualizan durante las hordas.'
], '07-partida.png','Figura 7. Jardín 5 × 9 cargado desde el JSON y catálogo con sprites originales.',alto=315)
usuario.append(tabla([['Zombi comprable','Precio base'],['Común','50'],['Caracono','100'],['Garrocha','100'],['Deportista','150'],['Bailón','150'],['Zombistein','350']],[ANCHO*.62,ANCHO*.38]))
usuario.append(PageBreak())
capitulo(usuario,'7. Recoger cerebros',[
    'Pulsa los iconos rosados sobre el jardín: cada uno entrega <b>25 cerebros</b> y desaparece al recogerlo. Si pasan <b>5 segundos de juego</b>, vence sin darte recursos.',
    'Los generadores esperan aleatoriamente entre 5 y 15 segundos. Hay 6 en Fácil, 5 en Medio y 4 en Difícil. Cada planta destruida deja entre 3 y 6 iconos adicionales.',
    'Los iconos pueden superponerse. Se recoge primero el que se ve encima; puedes pulsar varias veces para recoger los restantes. El clic en un cerebro no compra un zombi accidentalmente.'
], '08-combate-cerebros.png','Figura 8. Compra de un común y aparición de un cerebro por los generadores reales.',alto=345)
usuario.append(PageBreak())
capitulo(usuario,'8. Pausar, guardar y salir',[
    'Pulsa <b>Pausar / Reanudar</b>. Se detienen el reloj, los zombis, los proyectiles, la defensa automática y los generadores. El jardín no admite compras ni recolección mientras está pausado.',
    '<b>Reanudar:</b> cierra la ventana de pausa y continúa. <b>Guardar y salir:</b> guarda la partida en un archivo binario de tu cuenta y vuelve al menú.',
    'El archivo se identifica con fecha, hora y milisegundos. Si ocurre un error de escritura, la partida continúa pausada y puedes reintentar; no se muestra un guardado exitoso falso.'
], '09-pausa.png','Figura 9. Opciones de pausa. El tablero queda visible detrás y mantiene su estado.',alto=240)
usuario.append(p('Para cerrar una partida sin perder el avance, utiliza <b>Guardar y salir</b>. Al pulsar la X de la partida se abre la pausa.'))
usuario.append(PageBreak())
capitulo(usuario,'9. Reanudar una partida guardada',[
    '1. En el menú de la misma cuenta pulsa <b>Reanudar partida</b>. 2. Selecciona un archivo de la lista y confirma. 3. El tablero aparece <b>pausado</b>; pulsa <b>Reanudar</b> para continuar.',
    'Se recuperan tiempo, cerebros, entidades, vida, posiciones, proyectiles, congelación, digestión, podadoras y tiempos pendientes de la IA y los generadores. No se recarga el jardín original por encima de tu progreso.'
], '10-lista-guardados.png','Figura 10. Archivos de esa cuenta, ordenados del más reciente al más antiguo.',alto=230)
usuario += [imagen('12-reanudacion-pausada.png',ancho=340,alto=175),p('Figura 11. La carga exige una reanudación explícita.','Pie'),PageBreak()]
capitulo(usuario,'10. Tablero restaurado',[
    'Esta captura corresponde al tablero recuperado del archivo binario. Los procesos todavía no avanzan cuando termina la carga.',
    'Si guardas otra vez una partida cargada, primero se confirma el archivo nuevo y después se retira la copia anterior. Una partida completada se retira de la lista para evitar repetir ese guardado por accidente.'
], '11-tablero-restaurado.png','Figura 12. Tablero restaurado antes de reanudar.',alto=370)
usuario.append(PageBreak())
capitulo(usuario,'11. Dificultades, hordas y podadoras',[
    '<b>Fácil:</b> la computadora no agrega plantas. <b>Medio:</b> siembra cada 10 segundos. <b>Difícil:</b> evalúa amenazas cada 5 segundos y elige una única defensa según prioridades.',
    'La duración se divide en siete bloques iguales. En el bloque 4 los precios bajan 50%; en el 7 bajan 75%. Los demás bloques cobran el precio base. El aviso aparece arriba y el catálogo muestra el precio que se cobrará.',
    'Cada carril tiene una podadora. La primera llegada al extremo derecho la consume y elimina a los zombis de ese carril. Una llegada posterior por ese mismo carril produce tu victoria.'
])
usuario.append(tabla([['Ejemplo','Normal','Horda 1','Horda final'],['Común','50','25','13'],['Zombistein','350','175','88']],[ANCHO*.34,ANCHO*.22,ANCHO*.22,ANCHO*.22]))
usuario.append(p('Los precios fraccionarios se redondean hacia arriba. La pantalla y el cobro usan la misma regla.'))
usuario += [p('Victoria y derrota','Subtitulo'),p('Ganas al invadir la casa en un carril sin podadora disponible. Pierdes si el reloj llega a 00:00 antes de invadir. El resumen informa resultado, tiempo, plantas destruidas y zombis comprados.'),imagen('17-victoria.png',ancho=350,alto=210),p('Figura 13. Victoria obtenida mediante dos compras reales en una prueba corta.','Pie'),PageBreak()]
capitulo(usuario,'12. Resumen de una derrota',[
    'Cuando se agota el tiempo, el juego detiene la acción, muestra el resumen y registra una partida perdida. Al cerrar el resumen se vuelve al menú.',
    'Guardar y salir no cuenta como victoria ni derrota: las estadísticas se consolidan cuando la partida concluye.'
], '13-resultado.png','Figura 14. Derrota real en un nivel de demostración de 3 segundos.',alto=280)
usuario.append(PageBreak())
capitulo(usuario,'13. Consultar tus estadísticas',[
    'Desde el menú pulsa <b>Estadísticas</b>. En <b>Mi cuenta</b> aparecen partidas jugadas, ganadas y perdidas; plantas destruidas; zombis comprados; cerebros gastados; podadoras y mejor tiempo de victoria.',
    'También se muestran el porcentaje de victorias y el promedio de plantas destruidas por partida. Estos datos se conservan en el archivo de texto de tu cuenta.'
], '14-estadisticas.png','Figura 15. Cuenta de demostración después de concluir la partida corta.',alto=445)
usuario.append(PageBreak())
capitulo(usuario,'14. Ranking global',[
    'Selecciona la pestaña <b>Ranking global</b>. Se muestran hasta cinco jugadores en cada clasificación: invasores más rápidos (menor tiempo), mayores destructores (más plantas) y más exitosos (más victorias).',
    'Las cuentas sin victorias no aparecen en el ranking de rapidez. Si hay menos de cinco cuentas elegibles, la lista contiene solo las disponibles. Los empates se ordenan por nickname.'
], '15-ranking.png','Figura 16. Datos de demostración aislados para ilustrar los tres rankings; no se entregan como resultados de un jugador real.',alto=445)
usuario.append(PageBreak())
capitulo(usuario,'15. Reconocer los estados visuales',[
    'Las imágenes originales se conservan. Se agregaron variantes para mostrar la pérdida de brazo, casco y garrocha, el Zombistein sin Zombidito y un Bailarín distinguible del común.',
    'De arriba hacia abajo, la escena compara: común sano/dañado; cono/sin cono; casco/sin casco; garrocha/sin garrocha; garrocha dañada antes/después de saltar; Zombistein antes/después de lanzar; Bailón/Bailarín; común/Zombidito.'
], '16-estados-zombis.png','Figura 17. Escena de validación visual construida con el mismo panel del juego; no es una captura de una partida ordinaria.',alto=390)
usuario.append(PageBreak())
capitulo(usuario,'16. Resolver problemas frecuentes',[
    '<b>No aparecen mis cuentas:</b> abre el juego desde la carpeta donde está tu subcarpeta cuentas. Antes de cambiar de versión, conserva esa carpeta.',
    '<b>No encuentro una partida:</b> confirma que elegiste la cuenta correcta y que la partida se guardó, no solo se pausó. Las partidas completadas se retiran de la lista.',
    '<b>El nivel no inicia:</b> revisa el mensaje del lector. Cada carril debe tener igual número de columnas; las plantas deben usar identificadores permitidos; la duración debe ser positiva y los cerebros no negativos.',
    '<b>No puedo comprar:</b> revisa saldo, estado de pausa y selección del catálogo. Primero pulsa el tipo de zombi y después un carril.',
    '<b>Error al guardar:</b> comprueba que la carpeta del proyecto permita escritura y que haya espacio en disco. Mantén abierta la partida pausada hasta resolverlo.',
    '<b>Faltan imágenes:</b> utiliza el ZIP completo. El JAR ya contiene los recursos; no necesitas mover los PNG individualmente. Una carpeta externa Imagenes puede reemplazarlos voluntariamente.',
    '<b>Ventana grande en Windows:</b> el escalado de pantalla del sistema puede aumentar el tamaño físico. Los botones de menú/nueva partida mantienen sus dimensiones lógicas compactas.'
])
usuario.append(p('Para compilar desde las fuentes instala el <b>JDK</b>, no solo un entorno de ejecución. Ejecuta compilar.bat o sh compilar.sh. Los PDF no requieren instalar Python para leerlos ni para usar el juego.'))
guardar('Manual_de_usuario.pdf',usuario,'Manual de usuario — Zombis vs. Plantas')

tecnico = portada('Manual técnico','Arquitectura, algoritmos, concurrencia, persistencia y validación de la versión corregida.')
capitulo(tecnico,'1. Alcance y estructura del proyecto',[
    'Aplicación de escritorio Java Swing. El jugador compra zombis que avanzan de izquierda a derecha hacia una casa defendida por plantas. El jardín proviene de un JSON y cada cuenta mantiene partidas y estadísticas propias.',
    'El proyecto compila con <b>JDK 21 estándar</b>. No usa bibliotecas Java externas, Collections dinámicas, Lombok, Timer de Swing, volatile, synchronized, wait/notify ni java.util.concurrent. Se usan arreglos nativos que duplican su capacidad cuando es necesario.',
    'La documentación se genera fuera del juego con Python/ReportLab y Graphviz. Esas herramientas no son dependencias de compilación ni ejecución de la aplicación Java.'
])
tecnico.append(Preformatted('src/main/java/com/mycompany/proyecto2_plantasvszombies/\n  principal/   Arranque de la aplicación\n  vista/       Ventanas, paneles, recursos e hilos\n  modelo/      Partida, jardín, combate, cuentas y datos\n  plantas/     Jerarquía de defensas\n  zombis/      Jerarquía de atacantes\n  archivos/    JSON, cuentas, estadísticas y binarios\nsrc/main/resources/Imagenes/\nNiveles/     Archivos JSON de ejemplo\ndist/        JAR ejecutable\ntests/       Pruebas sin bibliotecas externas\nherramientas/ Capturas y generación de manuales',estilos['Codigo']))
tecnico += [p('Arranque y separación de responsabilidades','Subtitulo'),p('Proyecto2_PlantasVsZombies.main solo publica el arranque en SwingUtilities.invokeLater. Juego crea el gestor y abre VentanaInicio. Las ventanas interpretan acciones del usuario; Partida almacena estado; MotorCombate resuelve las reglas sin depender de Swing. Los gestores leen y escriben archivos.'),PageBreak()]
capitulo(tecnico,'2. UML del estado y del motor',[
    'El diagrama muestra datos persistentes y asociaciones. El rombo negro representa composición; una flecha simple, una referencia o uso. Los atributos llevan “−” y los métodos públicos “+”. El esquema omite getters repetidos para facilitar la lectura.'
])
tecnico += [imagen('nucleo.png',alto=560,carpeta=DOC/'diagramas'),p('Figura T1. Modelo de cuentas, partida, jardín, proyectiles y recursos. Las fuentes DOT y PNG se incluyen en documentacion/diagramas.','Pie'),PageBreak()]
capitulo(tecnico,'3. UML de las entidades',[
    'Planta y Zombi son clases abstractas. Las clases concretas fijan salud, daño, costo y cadencias. El motor usa el tipo concreto cuando una mecánica exige un tratamiento espacial especial. Las flechas con triángulo vacío representan herencia.',
    'Ejemplos de polimorfismo: recibirDanio descuenta primero un escudo en Caracono/Deportista; Pinchohierba ignora las mordidas; getDanio del Caracono retorna 15 después de perder el cono. Los atributos permanecen privados y los cambios de posición/velocidad auxiliares están protegidos en Zombi.'
])
tecnico += [imagen('plantas.png',alto=490,carpeta=DOC/'diagramas'),p('Figura T2a. Herencia de las siete plantas.','Pie'),PageBreak(),p('3.1. Jerarquía de zombis','Seccion'),imagen('zombis.png',alto=620,carpeta=DOC/'diagramas'),p('Figura T2b. Herencia de los ocho tipos concretos de zombis.','Pie'),PageBreak()]
capitulo(tecnico,'4. UML de interfaz, hilos y archivos',[
    'SeleccionCarril es una interfaz de callback: PanelJardinFondo notifica la fila y VentanaPartida recibe una lambda que solicita la compra. Serializable es una interfaz marcadora del JDK implementada por el estado persistente; no se usa para serializar ventanas o hilos.',
    'HilosPartida administra trabajadores independientes, pero entrega sus acciones al EDT. GestorPartidas separa el formato binario de la interfaz; LectorNivel contiene un analizador privado para el formato del enunciado.'
])
tecnico += [imagen('control.png',alto=370,carpeta=DOC/'diagramas'),p('Figura T3. Relaciones de control y persistencia.','Pie'),PageBreak()]
capitulo(tecnico,'5. Flujo general del motor',[
    'La ventana mide el tiempo mediante System.nanoTime. Limita cada paso a 0.15 s y acumula fracciones para descontar segundos completos del reloj. Se aplica el mismo paso al motor para conservar la relación entre simulación y contador.',
    'Si el reloj llega a cero, finaliza en derrota. En otro caso llama MotorCombate.actualizar, refresca paneles y comprueba si el motor detectó victoria.'
])
tecnico.append(Preformatted('fotograma en EDT:\n  si ventana cerrada o partida terminada: retornar\n  medir dt y actualizar instante\n  si pausada: retornar\n  limitar dt; acumular fracción y descontar segundos\n  si tiempo restante == 0: finalizar derrota\n  motor.actualizar(dt)\n  actualizar controles y repintar\n  si motor.hayVictoria(): finalizar victoria\n\nactualizar(dt):\n  si pausada/terminada o dt <= 0: retornar\n  avanzar tiempo de simulación\n  expirar cerebros; evaluar defensa cuando corresponda\n  actualizar animación de podadoras y capacidad de arreglos\n  activar Patatapum / terminar digestiones\n  disparar plantas y mover proyectiles\n  mover/atacar con zombis y resolver llegadas\n  resolver Pinchohierba, Patatapum y Carroñívora',estilos['Codigo']))
tecnico += [p('Al finalizar','Subtitulo'),p('La ventana marca terminada, detiene todos los trabajadores y deshabilita el catálogo. Registra una sola vez el resultado y los contadores, escribe las estadísticas, presenta el resumen y vuelve al menú. Guardar una partida no incrementa partidas jugadas: solo lo hace su conclusión.'),PageBreak()]
capitulo(tecnico,'6. Concurrencia y ciclo de vida de hilos',[
    '<b>Trabajadores reales:</b> cada ventana activa tiene un reloj y un Thread por generador: 6 en Fácil, 5 en Medio y 4 en Difícil. HilosPartida crea esos trabajadores como daemon. Cada uno espera 40 ms y publica su acción con SwingUtilities.invokeLater.',
    'El reloj publica fotograma(). El generador i publica actualizarGenerador(i). Cada hilo ejecuta su propio ciclo de espera/publicación. Los trabajadores no leen salud, dinero, pausas ni arreglos del modelo: todos esos accesos se ejecutan en el EDT.',
    'Los campos activo y sesion de HilosPartida también se leen y escriben solo en el EDT. Cada tarea captura el número de sesión con el que fue creada. Antes de ejecutarse, comprueba que siga siendo la sesión activa: así se descartan notificaciones antiguas tras una pausa o cierre.'
])
tecnico.append(tabla([['Transición','Acciones'],['Iniciar','Activar modo de generadores externos; crear reloj y N trabajadores.'],['Pausar','Marcar partida pausada; invalidar sesión; interrumpir todos los trabajadores.'],['Reanudar','Conservar modelo y fechas; reiniciar instante de fotograma; crear nueva sesión e hilos.'],['Guardar/cerrar/finalizar','Invalidar tareas pendientes e interrumpir trabajadores.'],['Cargar','Deserializar datos en pausa; no iniciar hilos hasta pulsar Reanudar.']],[ANCHO*.25,ANCHO*.75]))
tecnico += [p('Esperas en tiempo de juego','Subtitulo'),p('Cada generador mantiene una fecha proximoCerebro[i] relativa al reloj de simulación. Cuando vence, su trabajador solicita crear el icono y la fecha pasa a infinito. Al recogerlo o expirar se programa otra espera aleatoria de 5–15 s. Pausar no consume esas fechas, porque el reloj del modelo no avanza.'),p('Para pruebas sin ventanas se conserva un modo interno que evalúa generadores desde actualizar(). El modo externo es transient: nunca se guardan objetos Thread. La aplicación activa el modo externo al iniciar HilosPartida, por lo que los iconos no se generan dos veces.'),PageBreak()]
capitulo(tecnico,'7. Coordenadas, colisiones y casilla lógica',[
    'Las coordenadas del modelo no son píxeles. posicionX se mide en columnas; el centro del zombi es posicionX + 0.35. Su casilla lógica es <b>floor(posicionX + 0.35)</b>, limitada al rango 0..columnas−1. Cada entidad tiene así una única casilla para ataques de área, saltos e invocaciones.',
    'El panel transforma estas unidades según el rectángulo de césped. El dibujo y el clic usan la misma transformación para evitar seleccionar otro carril. La casa se muestra a la derecha, reflejando visualmente el fondo original.',
    'La velocidad base es 0.44 columnas/s multiplicada por el modificador del zombi. Una planta bloqueante limita el avance a columna − 0.30. Pinchohierba es transitable; Zombistein aplasta en vez de morder; Garrocha consume una sola oportunidad de salto y Nuez la bloquea.',
    'Los proyectiles avanzan 3 columnas/s. Se prueba el segmento entre su x anterior y la nueva, con margen 0.14; se elige el zombi vivo más cercano del mismo carril que intersecte el segmento. Un impacto elimina el proyectil. El hielo multiplica movimiento por 0.6 durante 4 s; otro impacto reinicia la fecha sin multiplicar otra reducción.'
])
tecnico += [p('Lanzamiento y animación','Subtitulo'),p('Al llegar al 50% de salud, Zombistein genera un único Zombidito. La posición de destino está antes de la penúltima casilla. Durante 0.8 s el panel interpola desde el lanzador al destino y dibuja una altura sinusoidal. Mientras vuela no muerde ni recibe ataques de plantas; al aterrizar vuelve al combate normal. Origen y fecha del vuelo son parte del estado binario. El Zombistein usa desde el lanzamiento una variante sin el pequeño en su espalda.'),p('Garrocha mantiene por separado haber saltado y perder el brazo: se cargan variantes con/sin arma y con/sin brazo. El estado de salud no cambia por seleccionar un sprite.'),PageBreak()]
capitulo(tecnico,'8. Catálogo y reglas de las plantas',[])
tecnico.append(tabla([['Planta','Salud','Ataque y mecánica'],['Lanzaguisantes','100','20 por proyectil; intervalo 1 s; dispara hacia los zombis detectados.'],['Patatapum','50','10 s de preparación; explosión de 100 a cada zombi de su casilla.'],['Hielaguisantes','100','15 por proyectil cada 1.5 s; ralentiza 40% durante 4 s.'],['Nuez','400','Bloquea incluso Garrocha. Estados: intacta ≥66%, grietas ≥35%, muy dañada <35%.'],['Carroñívora','100','Engulle en su casilla o frontal; digestión 8 s. Contra Zombistein aplica 200.'],['Pinchohierba','Inmune a mordidas','10 cada 0.5 s a zombis de su casilla; solo Zombistein la aplasta.'],['Bipetidora','100','15 por proyectil: uno frontal y dos traseros cada 1 s si hay blancos.']],[ANCHO*.24,ANCHO*.16,ANCHO*.60]))
tecnico += [Spacer(1,12),p('La salud de Pinchohierba se representa internamente con un marcador positivo, pero recibirDanio no reduce ese valor. Su destrucción por Zombistein retira la planta de la casilla directamente. Al destruir una planta se libera la casilla, se incrementa el contador y se crean 3–6 iconos de cerebro.'),p('Las fechas preparadaEn y digestionHasta se asocian a cada casilla. Una nueva siembra reinicia esas fechas; una Patatapum de la IA espera sus propios 10 s y no utiliza la edad global de la partida.'),PageBreak()]
capitulo(tecnico,'9. Catálogo y reglas de los zombis',[])
tecnico.append(tabla([['Zombi','HP/escudo','Costo','Velocidad','Mecánica'],['Común','100','50','1','Daño 15/s; brazo al 50%.'],['Caracono','100 + 100','100','1','Escudo primero; luego daño 15 de común.'],['Garrocha','120','100','1.7 → 1','Salta una vez salvo Nuez; pierde arma.'],['Deportista','100 + 150','150','2','Daño 10 cada 0.5 s; casco absorbe primero.'],['Bailón','150','150','0.8','Cada 5 s baila 1 s e invoca hasta 4 bailarines adyacentes libres.'],['Bailarín','100','Generado','1','Mecánica de común; sprite de bailarín propio.'],['Zombistein','300','350','0.7','Aplasta; al 50% lanza un Zombidito una vez.'],['Zombidito','50','Generado','1.7','Solo aparece por lanzamiento; no está en el catálogo.']],[ANCHO*.20,ANCHO*.16,ANCHO*.12,ANCHO*.14,ANCHO*.38]))
tecnico += [Spacer(1,12),p('Partida separa invocarZombi (cobra y cuenta una compra) de agregarZombiGenerado (sin cobro). Así los bailarines y Zombiditos no inflan los zombis comprados ni los cerebros gastados.'),p('Los escudos absorben primero el daño y trasladan el excedente a la salud base. Los zombis muertos permanecen como posiciones históricas del arreglo, pero el motor y el dibujo los omiten; no vuelven a actuar.'),PageBreak()]
capitulo(tecnico,'10. Defensa de la computadora',[
    'Fácil no siembra. Medio evalúa cada 10 s, selecciona un tipo al azar y recorre columnas desde la más cercana a la casa; entre filas desempata de arriba hacia abajo.',
    'Difícil evalúa cada 5 s y puede realizar una sola siembra. Para cada prioridad se examinan los zombis vivos antes de pasar a la regla siguiente. Si la acción no tiene una casilla válida, se continúa evaluando.'
])
tecnico.append(tabla([['Orden','Condición','Acción'],['1','Zombi a ≤4 casillas de casa','Nuez o Carroñívora en una casilla libre delante del zombi.'],['2','Zombi rebasó una planta','Bipetidora en la primera casilla libre a su izquierda.'],['3','Caracono o Zombistein','Patatapum o Carroñívora delante en ese carril.'],['4','Deportista','Hielaguisantes en ese carril.'],['5','Garrocha','Nuez en ese carril.'],['6','Más de tres zombis en un carril','Pinchohierba en ese carril.'],['7','Ninguna acción anterior ejecutable','Lanzaguisantes al fondo del carril con menos defensas y espacio libre.']],[ANCHO*.09,ANCHO*.34,ANCHO*.57]))
tecnico += [p('El enunciado menciona soles, pero no define su saldo, generación ni costos de plantas. Por ello la implementación decide por prioridad y disponibilidad de casillas; no inventa una tabla de costos de soles.'),PageBreak()]
capitulo(tecnico,'11. Hordas, podadoras y resultado',[
    'Bloque = min(7, 1 + floor((duracionTotal − tiempoRestante + fraccionReloj) × 7 / duracionTotal)). La fracción evita desplazar las fronteras al siguiente segundo entero.',
    'Bloques 1, 2, 3, 5 y 6: precio base. Bloque 4: mitad. Bloque 7: cuarta parte. Los costos fraccionarios se redondean hacia arriba. costoActual es la única regla usada por catálogo, comprobación de saldo y cobro.',
    'Cada carril empieza con una podadora disponible. La primera llegada la consume, elimina inmediatamente a todos los zombis vivos de ese carril y activa una animación de barrido. Una llegada posterior en ese carril marca victoria y termina la partida.',
    'Si el reloj llega a cero antes de esa invasión, la ventana registra derrota. El resumen muestra resultado, tiempo transcurrido, plantas destruidas, zombis comprados, cerebros gastados y podadoras activadas.'
])
tecnico += [p('Estadísticas derivadas','Subtitulo'),p('Porcentaje de victorias = ganadas × 100 / jugadas. Promedio de plantas = plantas destruidas / jugadas. Ambos retornan 0 si no hay partidas. La primera victoria establece el mejor tiempo; las siguientes solo lo sustituyen si son menores, incluido el caso válido de una victoria de menos de un segundo.'),p('Cada ranking copia las cuentas elegibles a un arreglo y ordena por inserción. Rapidez es ascendente, destrucción y victorias descendentes. Los empates se resuelven por nickname. Se muestran hasta cinco; en rapidez se excluyen las cuentas sin victorias.'),PageBreak()]
capitulo(tecnico,'12. Formato del nivel JSON',[
    'LectorNivel lee caracteres mediante java.io en UTF-8, con un límite de tamaño de 4 MB. Analizador mantiene un cursor, consume espacios entre tokens y exige las claves en el orden especificado. No es un analizador JSON de propósito general: implementa el formato del proyecto.'
])
tecnico.append(Preformatted('{\n  "duracionSegundos": 180,\n  "cerebrosIniciales": 150,\n  "jardin": [\n    ["VACIO", "NUEZ", "LANZAGUISANTES"],\n    ["PATATAPUM", "VACIO", "HIELAGUISANTES"]\n  ]\n}',estilos['Codigo']))
tecnico += [p('Validaciones','Subtitulo'),p('Duración positiva; cerebros no negativos; al menos una fila y una columna; filas de igual longitud; identificadores permitidos; enteros válidos; comas/corchetes/llaves correctos; ausencia de contenido sobrante. Las cadenas no necesitan escapes para los identificadores del enunciado.'),p('Los identificadores aceptados son VACIO, LANZAGUISANTES, PATATAPUM, HIELAGUISANTES, NUEZ, CARRONIVORA, PINCHOHIERBA y BIPETIDORA. Jardin transforma cada identificador en una planta concreta o una casilla vacía. El programa no modifica el archivo de nivel.'),p('El analizador usa arreglos temporales que crecen al doble y después se recortan a la longitud exacta. Los espacios entre filas no cambian la estructura del jardín.'),PageBreak()]
capitulo(tecnico,'13. Cuentas y estadísticas en texto',[
    'GestorCuentas valida 1–12 caracteres alfanuméricos y busca nombres con igualdad sensible a mayúsculas. Las nuevas carpetas se codifican con prefijo u- y cuatro dígitos hexadecimales por carácter: Ana y ana siguen siendo diferentes incluso en discos Windows.',
    'RutasCuentas reconoce también carpetas antiguas nombradas directamente con el nickname. Cada cuenta contiene un único estadisticas.txt y la subcarpeta partidas.'
])
tecnico.append(Preformatted('cuentas/\n  u-0041006e0061/           # Ana\n    estadisticas.txt\n    partidas/\n      AAAA-MM-DD_HH-mm-ss-SSS.bin\n\nPartidas Jugadas: 1\nPartidas Ganadas: 1\nPartidas Perdidas: 0\nPlantas Destruidas: 8\nZombis Invocados: 3\nCerebros Gastados: 150\nMejor Tiempo Victoria: 25\nPodadoras Activadas: 1',estilos['Codigo']))
tecnico += [p('Se leen las ocho líneas con BufferedReader y FileReader, extrayendo el entero después de “:”. La escritura usa FileWriter. En Java 21 la codificación por defecto es UTF-8. Primero se escribe un temporal y después se reemplaza el destino; los errores se comunican a la ventana.'),PageBreak()]
capitulo(tecnico,'14. Guardado y recuperación binaria',[
    'Solo se guarda una partida activa y pausada. GestorPartidas serializa un Estado con version = 1, MotorCombate y fraccionSegundo. El motor referencia Partida, jardín, plantas, zombis, proyectiles, cerebros, temporizadores, efectos y estado del generador aleatorio.',
    'Los arrays de fechas conservan disparos, mordiscos, congelación, baile, digestión, preparación, vuelos y próximas siembras. Las podadoras y contadores pertenecen a Partida. Las referencias compartidas se mantienen en el grafo de serialización.',
    'No se guardan JFrame, JDialog, Thread ni HilosPartida. El indicador de generadores externos es transient. Al cargar se comprueba tipo/version/cuenta, se vincula el objeto actual de la cuenta y se impone pausa. Los nuevos hilos solo se crean cuando el usuario reanuda.'
])
tecnico.append(Preformatted('Estado (version = 1)\n  motor -> MotorCombate\n    partida -> Cuenta, Jardin, Zombi[], podadoras, contadores\n    Proyectil[], Cerebro[]\n    fechas de efectos / generadores / defensa / vuelos\n    Random con su estado\n  fraccionSegundo',estilos['Codigo']))
tecnico += [p('Seguridad e integridad','Subtitulo'),p('ObjectInputFilter limita clases a las del proyecto, String, Random y arreglos/primitivos, y rechaza profundidad, referencias, tamaños o bytes excesivos. El guardado se escribe primero en un temporal y luego se renombra. No se elimina el archivo anterior de una partida cargada hasta confirmar el nuevo. Ante un fallo, el juego queda pausado.'),p('Para guardados previos a la animación aérea, los arrays nuevos de vuelo se inicializan de forma diferida. La compatibilidad se limita a esta familia de versión; no se promete leer formatos de otros programas.'),PageBreak()]
capitulo(tecnico,'15. Estados gráficos y recursos',[
    'Las 34 imágenes originales se mantienen sin cambiar su contenido. Se agregan ocho PNG transparentes para común sin brazo, Deportista sin casco, Garrocha sin arma, Garrocha sin brazo con/sin arma, Zombistein sin Zombidito, Bailarín y Zombidito.',
    'PanelJardinFondo selecciona el sprite a partir del estado del modelo. La Nuez agrega grietas según su porcentaje de salud; Patatapum indica preparación/activación; Carroñívora indica digestión. El hielo agrega una señal azul. Los cerebros se dibujan al final y se buscan en orden inverso al hacer clic para respetar superposición.',
    'Recursos busca primero una carpeta externa Imagenes y las rutas de recursos del proyecto; después busca dentro del JAR. La falta de un recurso deja un respaldo visual, sin alterar salud o reglas. Los botones del menú y nueva partida permanecen en 170 × 40; el selector de dificultad mide 120 × 28.'
])
tecnico += [imagen('16-estados-zombis.png',alto=330),p('Figura T4. Comparación de estados con el renderizador real. Las variantes adicionales se crearon a partir de las referencias originales mediante una herramienta de edición de imágenes; los originales se conservaron.','Pie'),PageBreak()]
capitulo(tecnico,'16. Compilación, pruebas y verificación',[
    '<b>Compilar:</b> compilar.bat en Windows o sh compilar.sh en Linux/macOS. javac usa --release 21, codificación UTF-8 y ningún classpath externo. jar incluye las clases y todos los recursos con la clase principal declarada.',
    '<b>Pruebas:</b> sh probar.sh ejecuta la lógica, la concurrencia y los estados gráficos. En una sesión gráfica, PROBAR_INTERFAZ=1 sh probar.sh agrega el flujo de interfaz y una partida completa.',
    'La validación actual reúne <b>52 comprobaciones de lógica/persistencia, 32 de concurrencia y 22 visuales/lanzamiento</b>. También se recorrió el flujo crear cuenta → nivel → cerebros → guardar → cargar en pausa → derrota → estadísticas, y se obtuvo una victoria real mediante dos compras por clic y el reloj de la aplicación.'
])
tecnico.append(tabla([['Prueba','Evidencia'],['Pruebas','JSON, escudos, hordas, IA, economía, pausa, podadoras, cuentas y binarios.'],['PruebasHilos','6/5/4 trabajadores reales, emisiones independientes, pausa, carga sin duplicados y ausencia de huérfanos.'],['PruebasVisuales','Selección de sprites por estado; vuelo, pausa y aterrizaje de Zombidito.'],['PruebasInterfaz','Compra/recolección por clic, pausa, guardado y regreso al menú.'],['PruebasPartidaCompleta','Primera llegada consume podadora; segunda logra victoria; resumen y persistencia.'],['CapturarManuales','Capturas reales y comprobación de carga exacta en pausa; partida corta con derrota.']],[ANCHO*.32,ANCHO*.68]))
tecnico += [p('Las pruebas se ejecutaron en Linux con JDK 21 y una pantalla virtual Xvfb. Los scripts de Windows se incluyen, pero no se ejecutaron en una máquina Windows. Las comprobaciones dan evidencia de los casos recorridos; no equivalen a certificar todos los posibles niveles o una nota académica.'),p('Consulta guiada del código','Subtitulo'),p('Para explicar la implementación, sigue en orden: main/Juego → VentanaNuevaPartida/LectorNivel → Partida/Jardin → HilosPartida/VentanaPartida → MotorCombate → GestorPartidas → Estadisticas/GestorCuentas. Usa las capturas y pruebas para relacionar cada regla con un comportamiento observable.')]
guardar('Manual_tecnico.pdf',tecnico,'Manual técnico — Zombis vs. Plantas')
print('PDF generados:', DOC/'Manual_de_usuario.pdf', DOC/'Manual_tecnico.pdf')
