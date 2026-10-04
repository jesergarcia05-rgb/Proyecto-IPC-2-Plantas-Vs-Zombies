package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Hielaguisantes extends Planta {

    private int danio;
    private int cadenciaDisparo;
    private int duracionCongelamiento;
    private double reduccionVelocidad;

    public Hielaguisantes(int fila, int columna) {

        super(100, fila, columna);

        danio = 15;
        cadenciaDisparo = 1500;
        duracionCongelamiento = 4000;
        reduccionVelocidad = 0.40;
    }

    public int getDanio() {
        return danio;
    }

    public int getCadenciaDisparo() {
        return cadenciaDisparo;
    }

    public int getDuracionCongelamiento() {
        return duracionCongelamiento;
    }

    public double getReduccionVelocidad() {
        return reduccionVelocidad;
    }
}