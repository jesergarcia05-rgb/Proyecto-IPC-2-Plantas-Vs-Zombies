package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Carronivora extends Planta {

    private int danio;
    private int tiempoDigestion;
    private boolean digiriendo;

    public Carronivora(int fila, int columna) {

        super(100, fila, columna);

        danio = 200;
        tiempoDigestion = 8000;
        digiriendo = false;
    }

    public int getDanio() {
        return danio;
    }

    public int getTiempoDigestion() {
        return tiempoDigestion;
    }

    public boolean estaDigiriendo() {
        return digiriendo;
    }

    public void iniciarDigestion() {
        digiriendo = true;
    }

    public void terminarDigestion() {
        digiriendo = false;
    }
}