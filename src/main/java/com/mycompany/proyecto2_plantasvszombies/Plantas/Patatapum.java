package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Patatapum extends Planta {

    private int danio;
    private int tiempoPreparacion;
    private boolean activa;

    public Patatapum(int fila, int columna) {

        super(50, fila, columna);

        danio = 100;
        tiempoPreparacion = 10000;
        activa = false;
    }

    public int getDanio() {
        return danio;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void activar() {
        activa = true;
    }
}