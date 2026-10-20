package com.mycompany.proyecto2_plantasvszombies.modelo;

import com.mycompany.proyecto2_plantasvszombies.plantas.Planta;

public class Casilla implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int fila;
    private int columna;
    private Planta planta;

    public Casilla(int fila, int columna) {

        this.fila = fila;
        this.columna = columna;
        this.planta = null;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public Planta getPlanta() {
        return planta;
    }

    public boolean estaVacia() {
        return planta == null;
    }

    public void colocarPlanta(Planta planta) {
        this.planta = planta;
    }

    public void quitarPlanta() {
        planta = null;
    }
}