package com.mycompany.proyecto2_plantasvszombies.modelo;

/** Posiciones expresadas en columnas/carriles, independientes del tamaño del panel. */
public class Cerebro implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final double x, y, vence;
    private final int generador;
    public Cerebro(double x, double y, double vence, int generador) {
        this.x = x; this.y = y; this.vence = vence; this.generador = generador;
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVence() { return vence; }
    public int getGenerador() { return generador; }
}
