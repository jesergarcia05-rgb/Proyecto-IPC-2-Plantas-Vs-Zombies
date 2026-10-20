package com.mycompany.proyecto2_plantasvszombies.vista;

import com.mycompany.proyecto2_plantasvszombies.modelo.Jardin;
import com.mycompany.proyecto2_plantasvszombies.modelo.Partida;
import com.mycompany.proyecto2_plantasvszombies.modelo.MotorCombate;
import com.mycompany.proyecto2_plantasvszombies.modelo.Proyectil;
import com.mycompany.proyecto2_plantasvszombies.zombis.Zombi;
import com.mycompany.proyecto2_plantasvszombies.zombis.ZombiComun;
import com.mycompany.proyecto2_plantasvszombies.zombis.ZombiCaracono;
import com.mycompany.proyecto2_plantasvszombies.zombis.ZombiGarrocha;
import com.mycompany.proyecto2_plantasvszombies.zombis.ZombiDeportista;
import com.mycompany.proyecto2_plantasvszombies.zombis.ZombiBailon;
import com.mycompany.proyecto2_plantasvszombies.zombis.ZombiBailarin;
import com.mycompany.proyecto2_plantasvszombies.zombis.Zombidito;
import com.mycompany.proyecto2_plantasvszombies.zombis.Zombistein;
import com.mycompany.proyecto2_plantasvszombies.plantas.Planta;
import com.mycompany.proyecto2_plantasvszombies.plantas.Lanzaguisantes;
import com.mycompany.proyecto2_plantasvszombies.plantas.Patatapum;
import com.mycompany.proyecto2_plantasvszombies.plantas.Hielaguisantes;
import com.mycompany.proyecto2_plantasvszombies.plantas.Nuez;
import com.mycompany.proyecto2_plantasvszombies.plantas.Carronivora;
import com.mycompany.proyecto2_plantasvszombies.plantas.Pinchohierba;
import com.mycompany.proyecto2_plantasvszombies.plantas.Bipetidora;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Muestra el fondo de cesped y las plantas del archivo JSON.
 * NO altera VentanaPartida.java, sus botones ni el archivo .form.
 * El tamano de la matriz SIEMPRE lo determina Jardin (JSON), NO la dificultad.
 */
public class PanelJardinFondo extends JPanel {

    private final BufferedImage fondo5x9;
    private final BufferedImage fondo4x4;
    private final BufferedImage fondo3x6;
    private final BufferedImage[] imagenesPlantas;
    private Jardin jardin;
    private Partida partida;
    private MotorCombate motorCombate;
    private SeleccionCarril seleccionCarril;
    private final BufferedImage[] imagenesZombis;
    private final BufferedImage imagenBailarin, imagenZombidito;
    private final BufferedImage comunSinBrazo, conoSinCono, cascoSinCasco, garrochaSinGarrocha;

    // Recibe el carril seleccionado; la ventana se encarga de comprar.
    public interface SeleccionCarril {
        void alSeleccionarCarril(int fila);
    }
    // true: la casa aparece a la derecha, como exige el enunciado.
    // Para ver la referencia sin reflejar, cambia a false.
    private boolean casaDerecha = true;

    public PanelJardinFondo() {
        // 5x9: la imagen exacta del jardin que proporcionaste.
        fondo5x9 = cargarImagen("jardin_5x9_original.png");
        // 4x4 y 3x6: versiones adaptadas visualmente al mismo estilo.
        fondo4x4 = cargarImagen("jardin_4x4.png");
        fondo3x6 = cargarImagen("jardin_3x6.png");

        String[] nombres = {
            "lanzaguisantes.png", "patatapum.png", "hielaguisantes.png",
            "nuez.png", "carronivora.png", "pinchohierba.png", "bipetidora.png"
        };
        imagenesPlantas = new BufferedImage[nombres.length];
        for (int i = 0; i < nombres.length; i++) {
            String nombreTransparente = nombres[i].replace(".png", "_sin_fondo.png");
            imagenesPlantas[i] = cargarImagenOpcional(nombreTransparente);
            if (imagenesPlantas[i] == null) {
                imagenesPlantas[i] = cargarImagen(nombres[i]);
            }
        }

        String[] nombresZombis = {
            "zombi.png", "caracono.png", "garrocha.png",
            "deportista.png", "bailon.png", "zombistein.png"
        };
        imagenesZombis = new BufferedImage[nombresZombis.length];
        for (int i = 0; i < nombresZombis.length; i++) {
            imagenesZombis[i] = cargarImagen(nombresZombis[i]);
        }

        imagenBailarin = cargarImagenOpcional("bailarin.png");
        imagenZombidito = cargarImagenOpcional("zombidito.png");
        comunSinBrazo = cargarImagenOpcional("zombi_sin_brazo.png");
        conoSinCono = cargarImagenOpcional("caracono_sin_cono.png");
        cascoSinCasco = cargarImagenOpcional("deportista_sin_casco.png");
        garrochaSinGarrocha = cargarImagenOpcional("garrocha_sin_garrocha.png");
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evento) {
                if (seleccionCarril == null || jardin == null || partida == null || partida.estaPausada() || partida.estaTerminada()) return;
                AreaCesped area = calcularAreaCesped();
                if (area == null) return;
                // Recoger primero el icono superior; nunca invocar a traves de un cerebro.
                if (motorCombate != null) {
                    for (int i = motorCombate.getCantidadCerebros() - 1; i >= 0; i--) {
                        var cerebro = motorCombate.getCerebro(i);
                        double x = area.x + cerebro.getX() * area.anchoCasilla;
                        double y = area.y + cerebro.getY() * area.altoCasilla;
                        if (Math.abs(evento.getX() - x) <= 15 && Math.abs(evento.getY() - y) <= 13) {
                            motorCombate.recogerCerebro(i); repaint(); return;
                        }
                    }
                }
                // Solo responde a clics dentro del cesped, no en la casa/margenes.
                if (evento.getX() < area.x || evento.getX() >= area.x + area.ancho
                        || evento.getY() < area.y || evento.getY() >= area.y + area.alto) {
                    return;
                }
                int fila = (int) ((evento.getY() - area.y) / area.altoCasilla);
                if (fila >= 0 && fila < jardin.getFilas()) {
                    seleccionCarril.alSeleccionarCarril(fila);
                }
            }
        });
    }

    private BufferedImage cargarImagen(String nombre) {
        BufferedImage imagen = cargarImagenOpcional(nombre);
        if (imagen == null) {
            // Se usara el dibujo vectorial de respaldo.
        }
        return imagen;
    }

    private BufferedImage cargarImagenOpcional(String nombre) {
        BufferedImage imagen = Recursos.imagen(nombre);
        if (imagen != null) return imagen;
        // Admite JPG y GIF conservando los nombres base del proyecto.
        if (nombre.endsWith(".png")) {
            imagen = Recursos.imagen(nombre.substring(0, nombre.length() - 4) + ".jpg");
            if (imagen == null) imagen = Recursos.imagen(nombre.substring(0, nombre.length() - 4) + ".gif");
        }
        return imagen;
    }

    public void setJardin(Jardin jardin) {
        this.jardin = jardin;
        repaint();
    }

    public void setPartida(Partida partida) {
        this.partida = partida;
        repaint();
    }

    public void setMotorCombate(MotorCombate motorCombate) {
        this.motorCombate = motorCombate;
        repaint();
    }

    public void setSeleccionCarril(SeleccionCarril seleccionCarril) {
        this.seleccionCarril = seleccionCarril;
    }

    // Coordenadas compartidas entre el clic y el dibujo.
    // Evita que la posicion del raton no coincida con el carril visual.
    private class AreaCesped {
        private double x, y, ancho, alto, anchoCasilla, altoCasilla;
        private boolean coincide;
        private BufferedImage fondo;
        private int origenX, origenY, anchoDibujo, altoDibujo;
    }

    private AreaCesped calcularAreaCesped() {
        if (jardin == null || jardin.getFilas() <= 0 || jardin.getColumnas() <= 0
                || getWidth() <= 0 || getHeight() <= 0) return null;

        int filas = jardin.getFilas();
        int columnas = jardin.getColumnas();
        AreaCesped a = new AreaCesped();
        a.fondo = fondo5x9;
        double xi = 0.142, yi = 0.120, xf = 0.956, yf = 0.902;
        a.coincide = filas == 5 && columnas == 9;
        if (filas == 4 && columnas == 4 && fondo4x4 != null) {
            a.fondo = fondo4x4;
            xi = 0.265; yi = 0.128; xf = 0.961; yf = 0.925;
            a.coincide = true;
        } else if (filas == 3 && columnas == 6 && fondo3x6 != null) {
            a.fondo = fondo3x6;
            xi = 0.181; yi = 0.172; xf = 0.978; yf = 0.887;
            a.coincide = true;
        }

        a.origenX = 0;
        a.origenY = 0;
        a.anchoDibujo = getWidth();
        a.altoDibujo = getHeight();
        if (a.fondo != null) {
            double escala = Math.min((double) getWidth() / a.fondo.getWidth(),
                    (double) getHeight() / a.fondo.getHeight());
            a.anchoDibujo = Math.max(1, (int) Math.round(a.fondo.getWidth() * escala));
            a.altoDibujo = Math.max(1, (int) Math.round(a.fondo.getHeight() * escala));
            a.origenX = (getWidth() - a.anchoDibujo) / 2;
            a.origenY = (getHeight() - a.altoDibujo) / 2;
        }
        if (casaDerecha) {
            double viejoXi = xi;
            xi = 1.0 - xf;
            xf = 1.0 - viejoXi;
        }
        if (a.fondo == null) { xi = 0.03; xf = 0.87; yi = 0.08; yf = 0.95; a.coincide = false; }
        a.x = a.origenX + a.anchoDibujo * xi;
        a.y = a.origenY + a.altoDibujo * yi;
        a.ancho = a.anchoDibujo * (xf - xi);
        a.alto = a.altoDibujo * (yf - yi);
        a.anchoCasilla = a.ancho / columnas;
        a.altoCasilla = a.alto / filas;
        return a;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int filas = jardin == null ? 5 : jardin.getFilas();
            int columnas = jardin == null ? 9 : jardin.getColumnas();
            if (filas <= 0 || columnas <= 0) return;

            AreaCesped area = calcularAreaCesped();
            if (area == null) return;
            BufferedImage fondo = area.fondo;
            boolean fondoCoincide = area.coincide;
            int origenX = area.origenX;
            int origenY = area.origenY;
            int anchoDibujo = area.anchoDibujo;
            int altoDibujo = area.altoDibujo;

            // Mantener la relacion de aspecto original y los margenes laterales.
            java.awt.GradientPaint pintura = new java.awt.GradientPaint(
                    0, 0, new Color(38, 82, 34),
                    Math.max(1, getWidth()), Math.max(1, getHeight()),
                    new Color(68, 115, 42));
            g2.setPaint(pintura);
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (fondo != null) {
                if (casaDerecha) {
                    g2.drawImage(fondo,
                            origenX + anchoDibujo, origenY,
                            origenX, origenY + altoDibujo,
                            0, 0, fondo.getWidth(), fondo.getHeight(), this);
                } else {
                    g2.drawImage(fondo, origenX, origenY,
                            anchoDibujo, altoDibujo, this);
                }
                g2.setColor(new Color(25, 70, 22));
                g2.drawRect(origenX, origenY,
                        Math.max(0, anchoDibujo - 1), Math.max(0, altoDibujo - 1));
            }

            double x0 = area.x;
            double y0 = area.y;
            double anchoCasilla = area.anchoCasilla;
            double altoCasilla = area.altoCasilla;

            // Para dimensiones distintas a los 3 fondos, dibuja una cuadricula
            // logica encima del cesped, usando el tamano real del JSON.
            if (!fondoCoincide) {
                for (int f = 0; f < filas; f++) {
                    for (int c = 0; c < columnas; c++) {
                        int xx = (int) Math.round(x0 + c * anchoCasilla);
                        int yy = (int) Math.round(y0 + f * altoCasilla);
                        int ww = (int) Math.ceil(anchoCasilla);
                        int hh = (int) Math.ceil(altoCasilla);
                        g2.setColor((f + c) % 2 == 0
                                ? new Color(107, 183, 52, 160)
                                : new Color(54, 123, 35, 160));
                        g2.fillRect(xx, yy, ww, hh);
                        g2.setColor(new Color(42, 93, 25, 130));
                        g2.drawRect(xx, yy, ww, hh);
                    }
                }
            }

            for (int f = 0; f < filas; f++) {
                for (int c = 0; c < columnas; c++) {
                    Planta planta = jardin.getCasilla(f, c).getPlanta();
                    if (motorCombate != null && motorCombate.recienAplastada(f, c)) {
                        int xx = (int)(x0 + (c + 0.5) * anchoCasilla), yy = (int)(y0 + (f + 0.5) * altoCasilla);
                        g2.setColor(new Color(115, 72, 24)); g2.fillOval(xx - 20, yy + 9, 40, 8);
                        g2.setColor(Color.ORANGE); g2.drawString("¡CRASH!", xx - 24, yy - 10);
                    }
                    if (planta == null || !planta.estaViva()) continue;

                    int tipo = indicePlanta(planta);
                    int cx = (int) Math.round(x0 + (c + 0.5) * anchoCasilla);
                    int cy = (int) Math.round(y0 + (f + 0.5) * altoCasilla);
                    // Solo usar hasta el 58% de la casilla: deja un margen
                    // alrededor de cada planta y evita que se vea apretada.
                    int anchoMaximo = Math.max(1, (int) Math.round(anchoCasilla * 0.58));
                    int altoMaximo = Math.max(1, (int) Math.round(altoCasilla * 0.58));

                    if (tipo >= 0 && imagenesPlantas[tipo] != null) {
                        BufferedImage imagen = imagenesPlantas[tipo];

                        // Mantener la proporcion original, sin estirar ni recortar.
                        double escala = Math.min(
                                (double) anchoMaximo / imagen.getWidth(),
                                (double) altoMaximo / imagen.getHeight());
                        int anchoFinal = Math.max(1,
                                (int) Math.round(imagen.getWidth() * escala));
                        int altoFinal = Math.max(1,
                                (int) Math.round(imagen.getHeight() * escala));

                        int xPlanta = cx - anchoFinal / 2;
                        int yPlanta = cy - altoFinal / 2;

                        // Los zombis avanzan de izquierda a derecha.
                        // Reflejar SOLO las plantas que deben mirar hacia la
                        // izquierda (hacia los zombis), no la Nuez, Patatapum
                        // ni Pinchohierba. El archivo PNG original no cambia.
                        if (debeMirarIzquierda(tipo)) {
                            g2.drawImage(imagen,
                                    xPlanta + anchoFinal, yPlanta,
                                    xPlanta, yPlanta + altoFinal,
                                    0, 0, imagen.getWidth(), imagen.getHeight(), this);
                        } else {
                            g2.drawImage(imagen, xPlanta, yPlanta,
                                    anchoFinal, altoFinal, this);
                        }
                    } else {
                        int tamanioMarcador = Math.min(anchoMaximo, altoMaximo);
                        dibujarMarcador(g2, cx, cy, tamanioMarcador, tipo);
                    }
                    dibujarEstadoPlanta(g2, planta, cx, cy, anchoMaximo, altoMaximo);
                    // Una linea roja indica la vida restante de una planta daniada.
                    int saludMax = saludMaximaPlanta(planta);
                    if (planta.getSalud() < saludMax && saludMax > 0) {
                        int w = Math.max(12, anchoMaximo);
                        int xx = cx - w / 2;
                        int yy = cy - altoMaximo / 2 - 5;
                        g2.setColor(new Color(130, 28, 25));
                        g2.fillRect(xx, yy, w, 3);
                        g2.setColor(new Color(75, 205, 68));
                        g2.fillRect(xx, yy, w * planta.getSalud() / saludMax, 3);
                    }
                }
            }
            // Los proyectiles y zombis se dibujan en las mismas coordenadas del JSON.
            dibujarProyectiles(g2, area);
            dibujarZombis(g2, area);
            dibujarPodadoras(g2, area);
            if (fondo == null) {
                g2.setColor(new Color(235, 203, 143));
                int hx = (int)(area.x + area.ancho + 12), hy = (int)(area.y + area.alto / 2);
                g2.fillRect(hx, hy - 30, 65, 60); g2.setColor(new Color(148, 46, 31));
                g2.fillPolygon(new int[]{hx - 5, hx + 32, hx + 70}, new int[]{hy - 30, hy - 65, hy - 30}, 3);
                g2.setColor(Color.BLACK); g2.drawString("CASA", hx + 12, hy + 8);
            }
            // Los cerebros siempre se dibujan al final, por encima del combate.
            dibujarCerebros(g2, area);
        } finally {
            g2.dispose();
        }
    }

    /** Una podadora dibujada en cada carril; no requiere PNG ni librerias. */
    private void dibujarPodadoras(Graphics2D g2, AreaCesped a) {
        if (motorCombate == null || jardin == null) return;
        int ancho = Math.max(16, (int) Math.round(Math.min(a.anchoCasilla, a.altoCasilla) * 0.42));
        int alto = Math.max(12, (int) Math.round(ancho * 0.48));
        for (int fila = 0; fila < jardin.getFilas(); fila++) {
            double progreso = motorCombate.progresoPodadora(fila);
            if (!motorCombate.tienePodadora(fila) && progreso < 0) continue;

            double xDerecha = a.x + a.ancho - ancho * 0.58;
            double xIzquierda = a.x + ancho * 0.58;
            double x = progreso < 0 ? xDerecha
                    : xDerecha + (xIzquierda - xDerecha) * progreso;
            int centroX = (int) Math.round(x);
            int centroY = (int) Math.round(a.y + (fila + 0.5) * a.altoCasilla);
            int izq = centroX - ancho / 2;
            int arriba = centroY - alto / 2;

            // Cuerpo rojo, ruedas oscuras y manubrio plateado.
            g2.setColor(new Color(43, 48, 46));
            g2.fillOval(izq + ancho / 9, arriba + alto - 2, Math.max(5, ancho / 4), Math.max(5, ancho / 4));
            g2.fillOval(izq + ancho * 5 / 8, arriba + alto - 2, Math.max(5, ancho / 4), Math.max(5, ancho / 4));
            g2.setColor(new Color(190, 36, 31));
            g2.fillRoundRect(izq, arriba, ancho, alto, 6, 6);
            g2.setColor(new Color(243, 90, 58));
            g2.fillRoundRect(izq + 2, arriba + 2, Math.max(2, ancho - 6), Math.max(2, alto / 3), 5, 5);
            g2.setColor(new Color(215, 222, 219));
            g2.drawLine(izq + ancho * 3 / 4, arriba, izq + ancho * 3 / 4 + 4, arriba - alto);
            g2.drawLine(izq + ancho * 3 / 4 + 4, arriba - alto,
                    izq + ancho * 3 / 4 + 11, arriba - alto);
        }
    }

    /** Los guisantes se representan como pequenos circulos verdes o azules. */
    private void dibujarProyectiles(Graphics2D g2, AreaCesped a) {
        if (motorCombate == null) return;
        int radio = Math.max(3, (int) Math.round(Math.min(a.anchoCasilla, a.altoCasilla) * 0.055));
        for (int i = 0; i < motorCombate.getCantidadProyectiles(); i++) {
            Proyectil p = motorCombate.getProyectil(i);
            int x = (int) Math.round(a.x + p.getX() * a.anchoCasilla);
            int y = (int) Math.round(a.y + (p.getFila() + 0.5) * a.altoCasilla);
            g2.setColor(p.esHielo() ? new Color(44, 153, 235) : new Color(69, 199, 29));
            g2.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);
            g2.setColor(p.esHielo() ? new Color(20, 78, 149) : new Color(29, 104, 18));
            g2.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);
        }
    }

    private int saludMaximaPlanta(Planta p) {
        if (p instanceof Nuez) return 400;
        if (p instanceof Patatapum) return 50;
        if (p instanceof Pinchohierba) return 1;
        return 100;
    }


    private void dibujarZombis(Graphics2D g2, AreaCesped a) {
        if (partida == null) return;
        for (int i = 0; i < partida.getCantidadZombis(); i++) {
            Zombi z = partida.getZombi(i); if (!z.estaVivo()) continue;
            int ancho = Math.max(12, (int)Math.min(a.anchoCasilla * 0.5, a.altoCasilla * 0.5));
            if (z instanceof Zombidito) ancho = Math.max(10, ancho * 2 / 3);
            if (z instanceof Zombistein) ancho = ancho * 5 / 4;
            int x = (int)(a.x + (z.getPosicionX() + 0.35) * a.anchoCasilla);
            int y = (int)(a.y + (z.getFila() + 0.5) * a.altoCasilla);
            boolean salto = motorCombate != null && motorCombate.estaSaltando(z);
            if (salto) y -= Math.max(12, (int)(Math.abs(Math.sin(motorCombate.getTiempoTranscurrido() * 9)) * 25));
            if (z instanceof ZombiBailon && ((ZombiBailon)z).estaBailando())
                x += (int)(Math.sin(motorCombate.getTiempoTranscurrido() * 20) * 5);
            BufferedImage sprite = spriteZombi(z);
            if (sprite != null) {
                double escala = Math.min(a.anchoCasilla * (z instanceof Zombidito ? 0.46 : 0.72) / sprite.getWidth(),
                    a.altoCasilla * (z instanceof Zombidito ? 0.56 : 0.83) / sprite.getHeight());
                int w = Math.max(1, (int)(sprite.getWidth() * escala)), h = Math.max(1, (int)(sprite.getHeight() * escala));
                // Se conserva la orientacion del proyecto original: avance hacia la derecha.
                g2.drawImage(sprite, x + w / 2, y - h / 2, x - w / 2, y + h / 2,
                    0, 0, sprite.getWidth(), sprite.getHeight(), this);
                String estado = "";
                if (z.perdioBrazo()) estado += "Sin brazo ";
                if (z instanceof ZombiCaracono && !((ZombiCaracono)z).tieneCono()) estado += "Sin cono ";
                if (z instanceof ZombiDeportista && !((ZombiDeportista)z).tieneCasco()) estado += "Sin casco ";
                if (z instanceof ZombiGarrocha && !((ZombiGarrocha)z).tieneGarrocha()) estado += "Sin garrocha ";
                if (motorCombate != null && motorCombate.estaCongelado(z)) {
                    g2.setColor(new Color(100, 225, 255, 90)); g2.fillOval(x - w / 2, y - h / 2, w, h);
                }
                if (!estado.isEmpty()) { g2.setColor(Color.YELLOW); g2.drawString(estado, x - w / 2, y - h / 2 - 5); }
                g2.setColor(new Color(130, 25, 25)); g2.fillRect(x - w / 2, y + h / 2 + 3, w, 4);
                g2.setColor(Color.GREEN); g2.fillRect(x - w / 2, y + h / 2 + 3, w * z.getSalud() / z.getSaludMaxima(), 4);
                continue;
            }
            g2.setStroke(new java.awt.BasicStroke(Math.max(2, ancho / 9)));
            g2.setColor(motorCombate != null && motorCombate.estaCongelado(z) ? Color.CYAN : new Color(125, 168, 100));
            g2.fillOval(x - ancho / 3, y - ancho, ancho * 2 / 3, ancho * 2 / 3);
            g2.setColor(Color.BLACK); g2.fillOval(x + ancho / 8, y - ancho * 3 / 4, 4, 4);
            Color traje = z instanceof ZombiBailarin ? new Color(206, 60, 215) : z instanceof ZombiBailon ? new Color(230, 70, 55) : new Color(74, 83, 113);
            g2.setColor(traje); g2.fillRect(x - ancho / 3, y - ancho / 3, ancho * 2 / 3, ancho * 2 / 3);
            g2.drawLine(x - ancho / 4, y + ancho / 3, x - ancho / 3, y + ancho * 2 / 3);
            g2.drawLine(x + ancho / 4, y + ancho / 3, x + ancho / 3, y + ancho * 2 / 3);
            g2.drawLine(x, y - ancho / 5, x + ancho * 2 / 3, y - ancho / 4);
            if (!z.perdioBrazo()) g2.drawLine(x, y, x - ancho * 2 / 3, y + ancho / 4);
            if (z instanceof ZombiCaracono && ((ZombiCaracono)z).tieneCono()) {
                g2.setColor(Color.ORANGE); g2.fillPolygon(new int[]{x - ancho / 2, x, x + ancho / 2}, new int[]{y - ancho, y - ancho * 3 / 2, y - ancho}, 3);
            }
            if (z instanceof ZombiDeportista && ((ZombiDeportista)z).tieneCasco()) {
                g2.setColor(new Color(180, 35, 35)); g2.fillArc(x - ancho / 2, y - ancho * 5 / 4, ancho, ancho * 2 / 3, 0, 180);
            }
            if (z instanceof ZombiGarrocha && ((ZombiGarrocha)z).tieneGarrocha()) {
                g2.setColor(new Color(235, 225, 180)); g2.drawLine(x + ancho / 2, y + ancho / 2, x + ancho, y - ancho * 3 / 2);
            }
            if (z instanceof Zombistein) {
                g2.setColor(new Color(137, 87, 37)); g2.fillRect(x + ancho / 2, y - ancho, ancho / 3, ancho);
                if (!((Zombistein)z).haLanzadoZombidito()) {
                    g2.setColor(new Color(150, 220, 110)); g2.fillOval(x - ancho, y - ancho, ancho / 2, ancho / 2);
                }
                if (motorCombate.estaLanzando(z)) { g2.setColor(Color.YELLOW); g2.drawString("¡Lanzamiento!", x - ancho, y - ancho * 3 / 2); }
            }
            int w = Math.max(15, ancho); g2.setColor(new Color(130, 25, 25)); g2.fillRect(x - w / 2, y + ancho, w, 4);
            g2.setColor(Color.GREEN); g2.fillRect(x - w / 2, y + ancho, w * z.getSalud() / z.getSaludMaxima(), 4);
        }
        g2.setStroke(new java.awt.BasicStroke(1));
    }
    private void dibujarEstadoPlanta(Graphics2D g, Planta p, int x, int y, int w, int h) {
        if (p instanceof Nuez && ((Nuez)p).obtenerEstadoVisual() > 1) {
            g.setColor(new Color(68, 36, 13)); g.setStroke(new java.awt.BasicStroke(3));
            g.drawPolyline(new int[]{x, x - w / 5, x + w / 6, x - w / 4}, new int[]{y - h / 2, y - h / 6, y + h / 8, y + h / 2}, 4);
            if (((Nuez)p).obtenerEstadoVisual() == 3) { g.drawLine(x - w / 3, y - h / 4, x + w / 3, y + h / 3); g.drawString("MUY DAÑADA", x - 35, y + h / 2 + 13); }
            g.setStroke(new java.awt.BasicStroke(1));
        }
        if (p instanceof Patatapum) {
            boolean activa = ((Patatapum)p).estaActiva();
            g.setColor(activa ? Color.RED : Color.GRAY); g.fillOval(x - 5, y - h / 2 - 9, 10, 10);
            g.drawString(activa ? "ACTIVA" : "PREPARANDO", x - 28, y + h / 2 + 13);
        }
        if (p instanceof Carronivora && ((Carronivora)p).estaDigiriendo()) {
            g.setColor(new Color(190, 40, 110)); g.drawString("DIGIRIENDO", x - 34, y + h / 2 + 13);
        }
    }
    private void dibujarCerebros(Graphics2D g, AreaCesped a) {
        if (motorCombate == null) return;
        for (int i = 0; i < motorCombate.getCantidadCerebros(); i++) {
            var c = motorCombate.getCerebro(i);
            int x = (int)(a.x + c.getX() * a.anchoCasilla), y = (int)(a.y + c.getY() * a.altoCasilla);
            g.setColor(new Color(250, 155, 189)); g.fillRoundRect(x - 15, y - 13, 30, 26, 12, 12);
            g.setColor(new Color(120, 35, 80)); g.drawRoundRect(x - 15, y - 13, 30, 26, 12, 12);
            g.drawLine(x, y - 11, x, y + 11); g.drawArc(x - 11, y - 8, 10, 12, 80, 210); g.drawArc(x + 1, y - 8, 10, 12, -110, 210);
        }
    }

    private BufferedImage spriteZombi(Zombi z) {
        if (z instanceof ZombiBailarin && imagenBailarin != null) return imagenBailarin;
        if (z instanceof Zombidito && imagenZombidito != null) return imagenZombidito;
        if (z instanceof ZombiComun && z.perdioBrazo() && comunSinBrazo != null) return comunSinBrazo;
        if (z instanceof ZombiCaracono && !((ZombiCaracono)z).tieneCono())
            return conoSinCono != null ? conoSinCono : imagenesZombis[0];
        if (z instanceof ZombiDeportista && !((ZombiDeportista)z).tieneCasco() && cascoSinCasco != null) return cascoSinCasco;
        if (z instanceof ZombiGarrocha && !((ZombiGarrocha)z).tieneGarrocha() && garrochaSinGarrocha != null) return garrochaSinGarrocha;
        int i = indiceZombi(z); return i < 0 ? null : imagenesZombis[i];
    }

    private int indiceZombi(Zombi z) {
        if (z instanceof ZombiComun || z instanceof ZombiBailarin || z instanceof Zombidito) return 0;
        if (z instanceof ZombiCaracono) return 1;
        if (z instanceof ZombiGarrocha) return 2;
        if (z instanceof ZombiDeportista) return 3;
        if (z instanceof ZombiBailon) return 4;
        if (z instanceof Zombistein) return 5;
        return -1;
    }

    // Indices: 0 Lanzaguisantes, 2 Hielaguisantes,
    // 4 Carronivora, 6 Bipetidora. Sus ataques miran hacia la izquierda.
    private boolean debeMirarIzquierda(int tipo) {
        return tipo == 0 || tipo == 2 || tipo == 4 || tipo == 6;
    }

    private int indicePlanta(Planta p) {
        if (p instanceof Lanzaguisantes) return 0;
        if (p instanceof Patatapum) return 1;
        if (p instanceof Hielaguisantes) return 2;
        if (p instanceof Nuez) return 3;
        if (p instanceof Carronivora) return 4;
        if (p instanceof Pinchohierba) return 5;
        if (p instanceof Bipetidora) return 6;
        return -1;
    }

    private void dibujarMarcador(Graphics2D g2, int x, int y, int tam, int tipo) {
        String[] nombres = {"LG", "PT", "HG", "NZ", "CR", "PH", "BP"};
        String texto = tipo >= 0 ? nombres[tipo] : "?";
        g2.setColor(new Color(239, 252, 217));
        g2.fillOval(x - tam / 2, y - tam / 2, tam, tam);
        g2.setColor(new Color(38, 91, 40));
        g2.drawOval(x - tam / 2, y - tam / 2, tam, tam);
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, tam / 4)));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, x - fm.stringWidth(texto) / 2,
                y + (fm.getAscent() - fm.getDescent()) / 2);
    }
}
