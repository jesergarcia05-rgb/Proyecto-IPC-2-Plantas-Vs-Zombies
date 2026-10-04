package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Nuez extends Planta {

    public Nuez(int fila, int columna) {

        super(400, fila, columna);
    }

    public int obtenerEstadoVisual() {

        double porcentaje =
                (getSalud() * 100.0) / 400.0;

        if (porcentaje >= 66) {
            return 1;
        }

        if (porcentaje >= 35) {
            return 2;
        }

        return 3;
    }
}