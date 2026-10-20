package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Nuez extends Planta {

    private int saludMaxima;

    public Nuez(int fila, int columna) {

        super(400, fila, columna);

        saludMaxima = 400;
    }

    public int getSaludMaxima() {
        return saludMaxima;
    }

    public int obtenerPorcentajeSalud() {

        return (getSalud() * 100) / saludMaxima;
    }

    public int obtenerEstadoVisual() {

        int porcentaje = obtenerPorcentajeSalud();

        if (porcentaje >= 66) {
            return 1;
        }

        if (porcentaje >= 35) {
            return 2;
        }

        return 3;
    }
}