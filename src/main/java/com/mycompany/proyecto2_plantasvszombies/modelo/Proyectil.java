package com.mycompany.proyecto2_plantasvszombies.modelo;

/**
 * Guisante en vuelo. x se mide en columnas del jardin (0 = borde izquierdo).
 * direccion: -1 hacia los zombis; 1 hacia la casa (cabeza trasera de Bipetidora).
 * El motor actualiza estos datos en el hilo de eventos de Swing.
 */
public class Proyectil implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final int fila;
    private double x;
    private final int direccion;
    private final int danio;
    private final boolean hielo;
    private final double velocidad;

    public Proyectil(int fila, double x, int direccion, int danio, boolean hielo) {
        this.fila = fila;
        this.x = x;
        this.direccion = direccion;
        this.danio = danio;
        this.hielo = hielo;
        this.velocidad = 3.0; // columnas por segundo; parametro propio del juego
    }

    public int getFila() { return fila; }
    public double getX() { return x; }
    public int getDireccion() { return direccion; }
    public int getDanio() { return danio; }
    public boolean esHielo() { return hielo; }
    public void avanzar(double segundos) { x += direccion * velocidad * segundos; }
}
