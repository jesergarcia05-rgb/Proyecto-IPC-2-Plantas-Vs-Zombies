package com.mycompany.proyecto2_plantasvszombies.zombis;

public class ZombiBailon extends Zombi {

    private final int intervaloBaile;
    private final int duracionBaile;
    private boolean bailando;

    public ZombiBailon(int fila, double posicionX) {

        super(150, 15, 150, 0.8, fila, posicionX);

        intervaloBaile = 5000;
        duracionBaile = 1000;
        bailando = false;
    }

    public int getIntervaloBaile() {
        return intervaloBaile;
    }

    public int getDuracionBaile() {
        return duracionBaile;
    }

    public boolean estaBailando() {
        return bailando;
    }

    public void iniciarBaile() {
        bailando = true;
    }

    public void terminarBaile() {
        bailando = false;
    }
}