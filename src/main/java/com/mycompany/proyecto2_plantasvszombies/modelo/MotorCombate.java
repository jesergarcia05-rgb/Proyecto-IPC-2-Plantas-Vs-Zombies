package com.mycompany.proyecto2_plantasvszombies.modelo;

import com.mycompany.proyecto2_plantasvszombies.plantas.*;
import com.mycompany.proyecto2_plantasvszombies.zombis.*;

/**
 * Logica de movimiento, colisiones y combate. No crea hilos ni toca Swing.
 * La ventana llama actualizar(segundos) desde el EDT cada ~40 ms; el unico
 * Thread del proyecto solo duerme y publica esos cambios con invokeLater.
 * Utiliza solo arreglos nativos y el JDK estandar; no introduce nuevos hilos.
 */
public class MotorCombate implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final Partida partida;
    private final Jardin jardin;
    private Proyectil[] proyectiles = new Proyectil[24];
    private int cantidadProyectiles;
    private double[][] proximoDisparo;
    private double[] proximoMordisco = new double[10];
    private double[] congeladoHasta = new double[10];
    private double[] siguienteBaile = new double[10];
    private double[] terminaBaile = new double[10];
    private double[][] digestionHasta;
    private double[][] proximoPincho;
    private double ahora;
    private final java.util.Random azar = new java.util.Random();
    private Cerebro[] cerebros = new Cerebro[24];
    private int cantidadCerebros;
    private double[] proximoCerebro;
    private double proximaDefensa;
    private double[][] preparadaEn;
    private double[][] aplastadaHasta;
    private double[] saltoHasta = new double[10];
    private double[] lanzamientoHasta = new double[10];
    private int plantasDestruidas;
    private boolean victoria;
    // La animacion es visual; el exterminio del carril es instantaneo.
    private final double[] animacionPodadoras;
    private static final double DURACION_PODADORA = 0.9;
    private static final double AVANCE_CASILLAS_POR_SEGUNDO = 0.44;

    public MotorCombate(Partida partida) {
        this.partida = partida;
        this.jardin = partida.getJardin();
        this.animacionPodadoras = new double[jardin.getFilas()];
        this.proximoDisparo = new double[jardin.getFilas()][jardin.getColumnas()];
        this.digestionHasta = new double[jardin.getFilas()][jardin.getColumnas()];
        this.proximoPincho = new double[jardin.getFilas()][jardin.getColumnas()];
        preparadaEn = new double[jardin.getFilas()][jardin.getColumnas()];
        aplastadaHasta = new double[jardin.getFilas()][jardin.getColumnas()];
        for (int f = 0; f < jardin.getFilas(); f++)
            for (int c = 0; c < jardin.getColumnas(); c++) preparadaEn[f][c] = 10;
        int procesos = partida.getDificultad().equals("Fácil") ? 6 : partida.getDificultad().equals("Medio") ? 5 : 4;
        proximoCerebro = new double[procesos];
        for (int i = 0; i < procesos; i++) proximoCerebro[i] = esperaCerebro();
        proximaDefensa = partida.getDificultad().equals("Medio") ? 10 : 5;
        // Primeros disparos y primeras mordidas necesitan transcurrir un instante.
        for (int f = 0; f < jardin.getFilas(); f++) {
            for (int c = 0; c < jardin.getColumnas(); c++) {
                proximoDisparo[f][c] = 0.3;
                proximoPincho[f][c] = 0.5;
            }
        }
    }

    public int getCantidadProyectiles() { return cantidadProyectiles; }
    public Proyectil getProyectil(int i) { return proyectiles[i]; }
    public int getPlantasDestruidas() { return plantasDestruidas; }
    public double getTiempoTranscurrido() { return ahora; }
    public boolean hayVictoria() { return victoria; }
    public boolean tienePodadora(int fila) { return partida.tienePodadora(fila); }
    public int getPodadorasActivadas() { return partida.getPodadorasActivadas(); }

    /** Progreso 0..1 de la podadora que barre de derecha a izquierda; -1 si no hay barrido. */
    public double progresoPodadora(int fila) {
        if (fila < 0 || fila >= animacionPodadoras.length
                || animacionPodadoras[fila] <= 0) return -1;
        return 1.0 - animacionPodadoras[fila] / DURACION_PODADORA;
    }
    public boolean estaCongelado(Zombi z) {
        int i = indiceZombi(z);
        return i >= 0 && ahora < congeladoHasta[i];
    }

    private int indiceZombi(Zombi z) {
        for (int i = 0; i < partida.getCantidadZombis(); i++) {
            if (partida.getZombi(i) == z) return i;
        }
        return -1;
    }

    /** Solo se llama con el juego activo, nunca durante pausa o tras terminar. */
    public void actualizar(double segundos) {
        if (segundos <= 0 || partida.estaPausada() || partida.estaTerminada()) return;
        segundos = Math.min(segundos, 0.15);
        ahora += segundos;
        actualizarEconomia();
        actualizarDefensa();
        for (int f = 0; f < animacionPodadoras.length; f++) {
            if (animacionPodadoras[f] > 0) {
                animacionPodadoras[f] = Math.max(0, animacionPodadoras[f] - segundos);
            }
        }
        asegurarEspacioZombis();
        activarPatatapumsYDigestion();
        dispararPlantas();
        moverProyectiles(segundos);
        moverYAtacarZombis(segundos);
        pinchohierbaYPatatapum();
    }

    private void asegurarEspacioZombis() {
        int n = partida.getCantidadZombis();
        if (n <= proximoMordisco.length) return;
        int nuevo = Math.max(n, proximoMordisco.length * 2);
        double[] mordisco = new double[nuevo];
        double[] hielo = new double[nuevo];
        double[] baile = new double[nuevo];
        double[] finBaile = new double[nuevo];
        System.arraycopy(proximoMordisco, 0, mordisco, 0, proximoMordisco.length);
        System.arraycopy(congeladoHasta, 0, hielo, 0, congeladoHasta.length);
        System.arraycopy(siguienteBaile, 0, baile, 0, siguienteBaile.length);
        System.arraycopy(terminaBaile, 0, finBaile, 0, terminaBaile.length);
        proximoMordisco = mordisco;
        congeladoHasta = hielo;
        siguienteBaile = baile;
        terminaBaile = finBaile;
        double[] salto = new double[nuevo], lanzamiento = new double[nuevo];
        System.arraycopy(saltoHasta, 0, salto, 0, saltoHasta.length);
        System.arraycopy(lanzamientoHasta, 0, lanzamiento, 0, lanzamientoHasta.length);
        saltoHasta = salto; lanzamientoHasta = lanzamiento;
    }

    private void activarPatatapumsYDigestion() {
        for (int f = 0; f < jardin.getFilas(); f++) {
            for (int c = 0; c < jardin.getColumnas(); c++) {
                Planta p = jardin.getCasilla(f, c).getPlanta();
                if (p instanceof Patatapum) {
                    Patatapum patata = (Patatapum) p;
                    if (!patata.estaActiva() && ahora >= preparadaEn[f][c]) {
                        patata.activar();
                    }
                }
                if (p instanceof Carronivora) {
                    Carronivora car = (Carronivora) p;
                    if (car.estaDigiriendo() && ahora >= digestionHasta[f][c]) {
                        car.terminarDigestion();
                    }
                }
            }
        }
    }

    private void dispararPlantas() {
        for (int f = 0; f < jardin.getFilas(); f++) {
            for (int c = 0; c < jardin.getColumnas(); c++) {
                Planta p = jardin.getCasilla(f, c).getPlanta();
                if (p == null || !p.estaViva() || ahora < proximoDisparo[f][c]) continue;
                double intervalo;
                if (p instanceof Lanzaguisantes) {
                    Lanzaguisantes lg = (Lanzaguisantes) p;
                    intervalo = lg.getCadenciaDisparo() / 1000.0;
                    if (hayZombiEnDireccion(f, c + 0.5, -1)) {
                        agregarProyectil(new Proyectil(f, c + 0.35, -1, lg.getDanio(), false));
                    }
                } else if (p instanceof Hielaguisantes) {
                    Hielaguisantes hg = (Hielaguisantes) p;
                    intervalo = hg.getCadenciaDisparo() / 1000.0;
                    if (hayZombiEnDireccion(f, c + 0.5, -1)) {
                        agregarProyectil(new Proyectil(f, c + 0.35, -1, hg.getDanio(), true));
                    }
                } else if (p instanceof Bipetidora) {
                    Bipetidora bp = (Bipetidora) p;
                    intervalo = bp.getCadenciaFrontal() / 1000.0;
                    if (hayZombiEnDireccion(f, c + 0.5, -1)) {
                        agregarProyectil(new Proyectil(f, c + 0.35, -1, bp.getDanio(), false));
                    }
                    if (hayZombiEnDireccion(f, c + 0.5, 1)) {
                        for (int k = 0; k < bp.getDisparosTraseros(); k++) {
                            // Pequeña separacion visual de la rafaga doble.
                            agregarProyectil(new Proyectil(f, c + 0.65 + 0.12 * k, 1,
                                    bp.getDanio(), false));
                        }
                    }
                } else {
                    continue;
                }
                proximoDisparo[f][c] = ahora + intervalo;
            }
        }
    }

    private boolean hayZombiEnDireccion(int fila, double x, int direccion) {
        for (int i = 0; i < partida.getCantidadZombis(); i++) {
            Zombi z = partida.getZombi(i);
            if (!z.estaVivo() || z.getFila() != fila) continue;
            double zx = z.getPosicionX() + 0.35;
            if (direccion < 0 && zx < x) return true;
            if (direccion > 0 && zx >= x) return true;
        }
        return false;
    }

    private void agregarProyectil(Proyectil p) {
        if (cantidadProyectiles == proyectiles.length) {
            Proyectil[] ampliado = new Proyectil[proyectiles.length * 2];
            System.arraycopy(proyectiles, 0, ampliado, 0, proyectiles.length);
            proyectiles = ampliado;
        }
        proyectiles[cantidadProyectiles++] = p;
    }

    private void quitarProyectil(int i) {
        for (int j = i + 1; j < cantidadProyectiles; j++) proyectiles[j - 1] = proyectiles[j];
        proyectiles[--cantidadProyectiles] = null;
    }

    private void moverProyectiles(double dt) {
        for (int i = cantidadProyectiles - 1; i >= 0; i--) {
            Proyectil p = proyectiles[i];
            double anterior = p.getX();
            p.avanzar(dt);
            double desde = Math.min(anterior, p.getX()) - 0.14;
            double hasta = Math.max(anterior, p.getX()) + 0.14;
            Zombi blanco = null;
            int indice = -1;
            double mejorDistancia = Double.POSITIVE_INFINITY;
            for (int j = 0; j < partida.getCantidadZombis(); j++) {
                Zombi z = partida.getZombi(j);
                if (!z.estaVivo() || z.getFila() != p.getFila()) continue;
                double centro = z.getPosicionX() + 0.35;
                if (centro >= desde && centro <= hasta) {
                    double d = Math.abs(centro - anterior);
                    if (d < mejorDistancia) { blanco = z; indice = j; mejorDistancia = d; }
                }
            }
            if (blanco != null) {
                blanco.recibirDanio(p.getDanio());
                if (p.esHielo() && blanco.estaVivo()) {
                    asegurarEspacioZombis();
                    congeladoHasta[indice] = ahora + 4.0;
                }
                quitarProyectil(i);
            } else if (p.getX() < -0.35 || p.getX() >= jardin.getColumnas() + 0.35) {
                quitarProyectil(i);
            }
        }
    }

    private void moverYAtacarZombis(double dt) {
        for (int i = 0; i < partida.getCantidadZombis(); i++) {
            if (victoria) return;
            asegurarEspacioZombis(); // Bailon y Zombistein pueden crear zombis durante este mismo ciclo.
            Zombi z = partida.getZombi(i);
            if (!z.estaVivo()) continue;
            int f = z.getFila();
            // Al perder la mitad de vida, Zombistein lanza al Zombidito una vez.
            if (z instanceof Zombistein) {
                Zombistein grande = (Zombistein) z;
                if (grande.debeLanzarZombidito()) {
                    double xLanzamiento = Math.max(0, jardin.getColumnas() - 2.35);
                    partida.agregarZombiGenerado(new Zombidito(f, xLanzamiento));
                    grande.marcarZombiditoComoLanzado();
                    lanzamientoHasta[i] = ahora + 0.8;
                }
            }
            // El Bailon convoca hasta cuatro bailarines si hay casillas adyacentes libres.
            if (z instanceof ZombiBailon) {
                ZombiBailon bailon = (ZombiBailon) z;
                if (siguienteBaile[i] == 0) siguienteBaile[i] = ahora + bailon.getIntervaloBaile() / 1000.0;
                if (bailon.estaBailando()) {
                    if (ahora < terminaBaile[i]) continue;
                    bailon.terminarBaile();
                }
                if (ahora >= siguienteBaile[i]) {
                    bailon.iniciarBaile();
                    terminaBaile[i] = ahora + bailon.getDuracionBaile() / 1000.0;
                    siguienteBaile[i] = ahora + bailon.getIntervaloBaile() / 1000.0;
                    invocarBailarines(bailon);
                    continue;
                }
            }
            double pos = z.getPosicionX();
            Planta obstaculo = null;
            int col = -1;
            double limiteCasa = jardin.getColumnas() - 0.70;
            double limite = limiteCasa;
            for (int c = 0; c < jardin.getColumnas(); c++) {
                Planta p = jardin.getCasilla(f, c).getPlanta();
                if (p == null || !p.estaViva() || p instanceof Pinchohierba) continue;
                double freno = Math.max(0.0, c - 0.30);
                // Una planta que ya quedo atras no bloquea el avance.
                if (freno >= pos - 0.04 && freno < limite) {
                    limite = freno;
                    obstaculo = p;
                    col = c;
                }
            }
            double distancia = AVANCE_CASILLAS_POR_SEGUNDO * dt;
            if (ahora < congeladoHasta[i]) distancia *= 0.60;
            double velocidad = z.getModificadorVelocidad();
            if (velocidad <= 0) continue;

            if (obstaculo != null && pos >= limite - 0.015) {
                // La Garrocha solo salta la primera planta si no es Nuez.
                if (z instanceof ZombiGarrocha && !((ZombiGarrocha) z).haSaltado()) {
                    ZombiGarrocha g = (ZombiGarrocha) z;
                    saltoHasta[i] = ahora + 0.65;
                    g.realizarSalto(); // pierde la garrocha y pasa a velocidad 1.0
                    if (!(obstaculo instanceof Nuez)) {
                        z.detenerAtaque();
                        // Aterriza en la casilla posterior a la planta saltada.
                        z.mover(Math.max(0, col + 0.70 - z.getPosicionX()));
                        continue;
                    }
                }
                if (z instanceof Zombistein) {
                    // El Zombistein aplasta al instante, en vez de morder.
                    aplastadaHasta[f][col] = ahora + 0.8;
                    destruirPlanta(f, col);
                    z.detenerAtaque();
                } else {
                    z.iniciarAtaque();
                    atacarConMordisco(z, i, obstaculo, f, col);
                }
            } else {
                z.detenerAtaque();
                double disponible = Math.max(0.0, limite - pos);
                z.mover(Math.min(distancia, disponible / velocidad));
            }
            // La llegada se comprueba tras cada avance y no depende del FPS.
            if (z.estaVivo() && z.getPosicionX() >= limiteCasa - 0.0001) {
                llegadaALaCasa(f);
            }
        }
    }

    /** Primera llegada: activar la podadora y destruir TODO el carril.
     *  Llegada posterior: victoria del jugador.
     */
    private void llegadaALaCasa(int fila) {
        if (partida.tienePodadora(fila)) {
            if (partida.activarPodadora(fila)) {
                animacionPodadoras[fila] = DURACION_PODADORA;
                for (int j = 0; j < partida.getCantidadZombis(); j++) {
                    Zombi otro = partida.getZombi(j);
                    if (otro.estaVivo() && otro.getFila() == fila) {
                        // Usa recibirDanio para respetar el polimorfismo y
                        // destruir incluso cascos, conos y Zombistein.
                        otro.recibirDanio(1_000_000);
                        otro.detenerAtaque();
                    }
                }
            }
        } else {
            victoria = true;
            partida.terminar();
        }
    }

    private void invocarBailarines(ZombiBailon bailon) {
        int fila = bailon.getFila();
        int columna = casillaActual(bailon);
        int[] cambioFila = {-1, 1, 0, 0};
        int[] cambioCol = {0, 0, -1, 1};
        for (int k = 0; k < 4; k++) {
            int f = fila + cambioFila[k];
            int c = columna + cambioCol[k];
            if (f < 0 || f >= jardin.getFilas() || c < 0 || c >= jardin.getColumnas()) continue;
            Planta ocupante = jardin.getCasilla(f, c).getPlanta();
            if (ocupante != null && ocupante.estaViva()) continue;
            double x = Math.max(0, c + 0.15);
            boolean ocupado = false;
            for (int i = 0; i < partida.getCantidadZombis(); i++) {
                Zombi otro = partida.getZombi(i);
                if (otro.estaVivo() && otro.getFila() == f && casillaActual(otro) == c) {
                    ocupado = true;
                    break;
                }
            }
            if (!ocupado) partida.agregarZombiGenerado(new ZombiBailarin(f, x));
        }
    }

    private void atacarConMordisco(Zombi z, int indice, Planta planta, int fila, int col) {
        if (planta == null || !planta.estaViva() || ahora < proximoMordisco[indice]) return;
        double intervalo = z instanceof ZombiDeportista
                ? ((ZombiDeportista) z).getIntervaloAtaque() / 1000.0 : 1.0;
        planta.recibirDanio(z.getDanio());
        proximoMordisco[indice] = ahora + intervalo;
        if (!planta.estaViva()) {
            destruirPlanta(fila, col);
            z.detenerAtaque();
        }
    }

    private void destruirPlanta(int fila, int col) {
        Planta planta = jardin.getCasilla(fila, col).getPlanta();
        if (planta == null) return;
        jardin.getCasilla(fila, col).quitarPlanta();
        plantasDestruidas++;
        int n = 3 + azar.nextInt(4);
        for (int k = 0; k < n; k++)
            agregarCerebro(new Cerebro(col + 0.25 + azar.nextDouble() * 0.5,
                    fila + 0.25 + azar.nextDouble() * 0.5, ahora + 5, -1));
    }

    private void pinchohierbaYPatatapum() {
        for (int f = 0; f < jardin.getFilas(); f++) {
            for (int c = 0; c < jardin.getColumnas(); c++) {
                Planta p = jardin.getCasilla(f, c).getPlanta();
                if (p == null || !p.estaViva()) continue;
                if (p instanceof Pinchohierba && ahora >= proximoPincho[f][c]) {
                    for (int i = 0; i < partida.getCantidadZombis(); i++) {
                        Zombi z = partida.getZombi(i);
                        if (z.estaVivo() && z.getFila() == f && casillaActual(z) == c) {
                            if (z instanceof Zombistein) {
                                aplastadaHasta[f][c] = ahora + 0.8;
                                destruirPlanta(f, c);
                                break;
                            }
                            z.recibirDanio(((Pinchohierba) p).getDanio());
                        }
                    }
                    proximoPincho[f][c] = ahora + ((Pinchohierba) p).getIntervaloDanio() / 1000.0;
                } else if (p instanceof Patatapum && ((Patatapum) p).estaActiva()) {
                    boolean explotar = false;
                    for (int i = 0; i < partida.getCantidadZombis(); i++) {
                        Zombi z = partida.getZombi(i);
                        if (z.estaVivo() && z.getFila() == f && casillaActual(z) == c) {
                            z.recibirDanio(((Patatapum) p).getDanio());
                            explotar = true;
                        }
                    }
                    if (explotar) destruirPlanta(f, c);
                } else if (p instanceof Carronivora && !((Carronivora) p).estaDigiriendo()) {
                    for (int i = 0; i < partida.getCantidadZombis(); i++) {
                        Zombi z = partida.getZombi(i);
                        if (!z.estaVivo() || z.getFila() != f) continue;
                        int casilla = casillaActual(z);
                        if (casilla != c && casilla != c - 1) continue;
                        if (z instanceof Zombistein) {
                            z.recibirDanio(((Carronivora) p).getDanioContraZombistein());
                        } else {
                            int total = z.getSalud();
                            if (z instanceof ZombiCaracono) total += ((ZombiCaracono) z).getEscudoCono();
                            if (z instanceof ZombiDeportista) total += ((ZombiDeportista) z).getEscudoCasco();
                            z.recibirDanio(total);
                        }
                        ((Carronivora) p).iniciarDigestion();
                        digestionHasta[f][c] = ahora + ((Carronivora) p).getTiempoDigestion() / 1000.0;
                        break;
                    }
                }
            }
        }
    }

    /** Traduce posicion continua a una casilla unica, util para ataques de area. */
    public int casillaActual(Zombi z) {
        int c = (int) Math.floor(z.getPosicionX() + 0.35);
        return Math.max(0, Math.min(jardin.getColumnas() - 1, c));
    }

    public Partida getPartida() { return partida; }
    public int getCantidadCerebros() { return cantidadCerebros; }
    public Cerebro getCerebro(int i) { return cerebros[i]; }
    public int getCantidadGeneradores() { return proximoCerebro.length; }
    public boolean estaSaltando(Zombi z) { int i = indiceZombi(z); return i >= 0 && i < saltoHasta.length && ahora < saltoHasta[i]; }
    public boolean estaLanzando(Zombi z) { int i = indiceZombi(z); return i >= 0 && i < lanzamientoHasta.length && ahora < lanzamientoHasta[i]; }
    public boolean recienAplastada(int f, int c) { return ahora < aplastadaHasta[f][c]; }
    private double esperaCerebro() { return 5 + azar.nextInt(11); }
    private void agregarCerebro(Cerebro cerebro) {
        if (cantidadCerebros == cerebros.length) {
            Cerebro[] a = new Cerebro[cerebros.length * 2];
            System.arraycopy(cerebros, 0, a, 0, cantidadCerebros); cerebros = a;
        }
        cerebros[cantidadCerebros++] = cerebro;
    }
    private void quitarCerebro(int i) {
        int generador = cerebros[i].getGenerador();
        if (generador >= 0) proximoCerebro[generador] = ahora + esperaCerebro();
        for (int k = i + 1; k < cantidadCerebros; k++) cerebros[k - 1] = cerebros[k];
        cerebros[--cantidadCerebros] = null;
    }
    public boolean recogerCerebro(int i) {
        if (partida.estaPausada() || partida.estaTerminada() || i < 0 || i >= cantidadCerebros) return false;
        if (ahora >= cerebros[i].getVence()) { quitarCerebro(i); return false; }
        quitarCerebro(i); partida.agregarCerebros(25); return true;
    }
    private void actualizarEconomia() {
        for (int i = cantidadCerebros - 1; i >= 0; i--)
            if (ahora >= cerebros[i].getVence()) quitarCerebro(i);
        for (int i = 0; i < proximoCerebro.length; i++) {
            if (ahora >= proximoCerebro[i]) {
                agregarCerebro(new Cerebro(0.2 + azar.nextDouble() * Math.max(0.1, jardin.getColumnas() - 0.4),
                    0.2 + azar.nextDouble() * Math.max(0.1, jardin.getFilas() - 0.4), ahora + 5, i));
                // Cada proceso espera a que su icono se recoja o expire antes de reiniciar.
                proximoCerebro[i] = Double.POSITIVE_INFINITY;
            }
        }
    }
    private boolean sembrar(String tipo, int f, int c) {
        if (!jardin.sembrar(tipo, f, c)) return false;
        preparadaEn[f][c] = ahora + 10;
        proximoDisparo[f][c] = ahora + 0.3;
        proximoPincho[f][c] = ahora + 0.5;
        digestionHasta[f][c] = 0;
        return true;
    }
    private boolean sembrarEnCarril(String tipo, Zombi z) {
        int inicio = casillaActual(z) + 1;
        for (int c = inicio; c < jardin.getColumnas(); c++)
            if (sembrar(tipo, z.getFila(), c)) return true;
        return false;
    }
    private void actualizarDefensa() {
        String dificultad = partida.getDificultad();
        if (dificultad.equals("Fácil") || ahora < proximaDefensa) return;
        proximaDefensa = ahora + (dificultad.equals("Medio") ? 10 : 5);
        if (dificultad.equals("Medio")) {
            String[] tipos = {"LANZAGUISANTES", "PATATAPUM", "HIELAGUISANTES", "NUEZ", "CARRONIVORA", "PINCHOHIERBA", "BIPETIDORA"};
            String tipo = tipos[azar.nextInt(tipos.length)];
            for (int c = jardin.getColumnas() - 1; c >= 0; c--)
                for (int f = 0; f < jardin.getFilas(); f++) if (sembrar(tipo, f, c)) return;
            return;
        }
        // Cada regla se evalua en TODO el tablero antes de pasar a la siguiente.
        for (int regla = 0; regla < 6; regla++) {
            for (int i = 0; i < partida.getCantidadZombis(); i++) {
                Zombi z = partida.getZombi(i); if (!z.estaVivo()) continue;
                int f = z.getFila(), columna = casillaActual(z);
                if (regla == 0 && jardin.getColumnas() - 1 - columna <= 4) {
                    if (sembrarEnCarril(azar.nextBoolean() ? "NUEZ" : "CARRONIVORA", z)) return;
                } else if (regla == 1) {
                    boolean supero = false;
                    for (int c = 0; c < columna; c++)
                        if (!jardin.getCasilla(f, c).estaVacia()) supero = true;
                    if (supero) for (int c = columna - 1; c >= 0; c--)
                        if (sembrar("BIPETIDORA", f, c)) return;
                } else if (regla == 2 && (z instanceof ZombiCaracono || z instanceof Zombistein)) {
                    if (sembrarEnCarril(azar.nextBoolean() ? "PATATAPUM" : "CARRONIVORA", z)) return;
                } else if (regla == 3 && z instanceof ZombiDeportista) {
                    if (sembrarEnCarril("HIELAGUISANTES", z)) return;
                } else if (regla == 4 && z instanceof ZombiGarrocha) {
                    if (sembrarEnCarril("NUEZ", z)) return;
                } else if (regla == 5) {
                    int n = 0;
                    for (int j = 0; j < partida.getCantidadZombis(); j++)
                        if (partida.getZombi(j).estaVivo() && partida.getZombi(j).getFila() == f) n++;
                    if (n > 3 && sembrarEnCarril("PINCHOHIERBA", z)) return;
                }
            }
        }
        int mejorFila = -1, minimo = Integer.MAX_VALUE;
        for (int f = 0; f < jardin.getFilas(); f++) {
            int defensas = 0; boolean libre = false;
            for (int c = 0; c < jardin.getColumnas(); c++)
                if (jardin.getCasilla(f, c).estaVacia()) libre = true; else defensas++;
            if (libre && defensas < minimo) { minimo = defensas; mejorFila = f; }
        }
        if (mejorFila >= 0) for (int c = jardin.getColumnas() - 1; c >= 0; c--)
            if (sembrar("LANZAGUISANTES", mejorFila, c)) return;
    }
}
