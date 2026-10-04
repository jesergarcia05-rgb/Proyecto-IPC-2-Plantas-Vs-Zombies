package com.mycompany.proyecto2_plantasvszombies.plantas;

public abstract class Planta {

    private int salud;
    private int fila;
    private int columna;

    public Planta(int salud, int fila, int columna) {
        this.salud = salud;
        this.fila = fila;
        this.columna = columna;
    }

    public int getSalud() {
        return salud;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public void recibirDanio(int danio) {

        salud = salud - danio;

        if (salud < 0) {
            salud = 0;
        }
    }

    public boolean estaViva() {
        return salud > 0;
    }
}