package com.mycompany.proyecto2_plantasvszombies.modelo;

import com.mycompany.proyecto2_plantasvszombies.zombis.Zombi;

/**
 * Guarda el estado de una partida. No maneja la interfaz ni crea hilos.
 */
public class Partida implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Cuenta cuenta;
    private final int duracionTotal;
    private int zombisComprados;
    private int cerebrosGastados;
    private double fraccionReloj;
    private final Jardin jardin;
    private final String dificultad;

    private int cerebros;
    private int tiempoRestante;
    private Zombi[] zombis;
    private int cantidadZombis;
    private boolean pausada;
    private boolean terminada;
    // Una podadora por carril. Este estado es parte de la partida y sera
    // necesario guardarlo en el archivo binario en una etapa posterior.
    private final boolean[] podadorasDisponibles;
    private int podadorasActivadas;

    public Partida(Cuenta cuenta, Jardin jardin,
                   ConfiguracionNivel configuracion, String dificultad) {
        if (cuenta == null || jardin == null || configuracion == null
                || dificultad == null) {
            throw new IllegalArgumentException("Faltan datos de la partida.");
        }

        this.cuenta = cuenta;
        this.duracionTotal = configuracion.getDuracionSegundos();
        this.jardin = jardin;
        this.dificultad = dificultad;
        this.cerebros = configuracion.getCerebrosIniciales();
        this.tiempoRestante = configuracion.getDuracionSegundos();
        this.zombis = new Zombi[10];
        this.cantidadZombis = 0;
        this.pausada = false;
        this.terminada = false;
        this.podadorasDisponibles = new boolean[jardin.getFilas()];
        for (int f = 0; f < podadorasDisponibles.length; f++) {
            podadorasDisponibles[f] = true;
        }
        this.podadorasActivadas = 0;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }

    public Jardin getJardin() {
        return jardin;
    }

    public String getDificultad() {
        return dificultad;
    }

    public int getCerebros() {
        return cerebros;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public int getCantidadZombis() {
        return cantidadZombis;
    }

    public Zombi getZombi(int indice) {
        if (indice < 0 || indice >= cantidadZombis) {
            throw new IndexOutOfBoundsException("No existe ese zombi.");
        }
        return zombis[indice];
    }

    public boolean estaPausada() {
        return pausada;
    }

    public boolean estaTerminada() {
        return terminada;
    }

    public void pausar() {
        if (!terminada) {
            pausada = true;
        }
    }

    public void reanudar() {
        if (!terminada) {
            pausada = false;
        }
    }

    public void terminar() {
        terminada = true;
    }

    /** Inicia cada carril con una podadora disponible. */
    public boolean tienePodadora(int fila) {
        return fila >= 0 && fila < podadorasDisponibles.length
                && podadorasDisponibles[fila];
    }

    /** La primera llegada consume la podadora; las siguientes no. */
    public boolean activarPodadora(int fila) {
        if (pausada || terminada || !tienePodadora(fila)) return false;
        podadorasDisponibles[fila] = false;
        podadorasActivadas++;
        return true;
    }

    public int getPodadorasActivadas() {
        return podadorasActivadas;
    }

    public void descontarUnSegundo() {
        if (!pausada && !terminada && tiempoRestante > 0) {
            tiempoRestante--;
        }
    }

    public void agregarCerebros(int cantidad) {
        if (cantidad > 0 && !pausada && !terminada) {
            cerebros += cantidad;
        }
    }

    /**
     * Invoca un zombi comprado por el jugador.
     * Devuelve false si no hay dinero o si la partida no permite jugar.
     */
    public boolean invocarZombi(Zombi zombi) {
        if (zombi == null || pausada || terminada
                || zombi.getCosto() <= 0
                || zombi.getFila() < 0
                || zombi.getFila() >= jardin.getFilas()
                || costoActual(zombi.getCosto()) > cerebros) {
            return false;
        }

        guardarEnArreglo(zombi);
        int costo = costoActual(zombi.getCosto());
        cerebros -= costo;
        cerebrosGastados += costo;
        zombisComprados++;
        return true;
    }

    /** Permite registrar zombis invocados por otro zombi, sin cobrar. */
    public boolean agregarZombiGenerado(Zombi zombi) {
        if (zombi == null || pausada || terminada
                || zombi.getFila() < 0
                || zombi.getFila() >= jardin.getFilas()) {
            return false;
        }
        guardarEnArreglo(zombi);
        return true;
    }

    private void guardarEnArreglo(Zombi zombi) {
        if (cantidadZombis == zombis.length) {
            Zombi[] ampliado = new Zombi[zombis.length * 2];
            for (int i = 0; i < zombis.length; i++) {
                ampliado[i] = zombis[i];
            }
            zombis = ampliado;
        }
        zombis[cantidadZombis] = zombi;
        cantidadZombis++;
    }

    public void vincularCuenta(Cuenta actual) {
        if (actual == null || !actual.getNickname().equals(cuenta.getNickname()))
            throw new IllegalArgumentException("La partida pertenece a otra cuenta.");
        cuenta = actual;
    }
    public void setFraccionReloj(double fraccion) {
        if (fraccion >= 0 && fraccion < 1) fraccionReloj = fraccion;
    }
    public int getDuracionTotal() { return duracionTotal; }
    public int getZombisComprados() { return zombisComprados; }
    public int getCerebrosGastados() { return cerebrosGastados; }
    public int getBloque() {
        double transcurrido = duracionTotal - tiempoRestante + fraccionReloj;
        return Math.min(7, 1 + (int)(transcurrido * 7.0 / duracionTotal));
    }
    // El enunciado no fija redondeo para costos fraccionarios: se redondea arriba.
    public int costoActual(int base) {
        int bloque = getBloque();
        return bloque == 7 ? (base + 3) / 4 : bloque == 4 ? (base + 1) / 2 : base;
    }
}
