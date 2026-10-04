package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Pinchohierba extends Planta {

    private int danio;
    private int intervaloDanio;

    public Pinchohierba(int fila, int columna) {

        super(1, fila, columna);

        danio = 10;
        intervaloDanio = 500;
    }

    public int getDanio() {
        return danio;
    }

    public int getIntervaloDanio() {
        return intervaloDanio;
    }

    @Override
    public void recibirDanio(int danio) {
        // Los zombis normales no pueden dañarla.
    }
}