package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Lanzaguisantes extends Planta {

    private final int danio;
    private final int cadenciaDisparo;

    public Lanzaguisantes(int fila, int columna) {

        super(100, fila, columna);

        danio = 20;
        cadenciaDisparo = 1000;
    }

    public int getDanio() {
        return danio;
    }

    public int getCadenciaDisparo() {
        return cadenciaDisparo;
    }
}