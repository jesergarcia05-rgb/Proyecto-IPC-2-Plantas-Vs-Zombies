package com.mycompany.proyecto2_plantasvszombies.modelo;

import com.mycompany.proyecto2_plantasvszombies.plantas.Planta;
import com.mycompany.proyecto2_plantasvszombies.plantas.Lanzaguisantes;
import com.mycompany.proyecto2_plantasvszombies.plantas.Patatapum;
import com.mycompany.proyecto2_plantasvszombies.plantas.Hielaguisantes;
import com.mycompany.proyecto2_plantasvszombies.plantas.Nuez;
import com.mycompany.proyecto2_plantasvszombies.plantas.Carronivora;
import com.mycompany.proyecto2_plantasvszombies.plantas.Pinchohierba;
import com.mycompany.proyecto2_plantasvszombies.plantas.Bipetidora;

public class Jardin {

    private Casilla[][] casillas;
    private int filas;
    private int columnas;

    public Jardin(String[][] configuracion) {

        filas = configuracion.length;
        columnas = configuracion[0].length;

        casillas = new Casilla[filas][columnas];

        crearJardin(configuracion);
    }

    private void crearJardin(String[][] configuracion) {

        for (int fila = 0; fila < filas; fila++) {

            for (int columna = 0; columna < columnas; columna++) {

                casillas[fila][columna] =
                        new Casilla(fila, columna);

                Planta planta =
                        crearPlanta(
                                configuracion[fila][columna],
                                fila,
                                columna
                        );

                if (planta != null) {

                    casillas[fila][columna]
                            .colocarPlanta(planta);
                }
            }
        }
    }

    private Planta crearPlanta(
            String tipo,
            int fila,
            int columna) {

        if (tipo.equals("VACIO")) {
            return null;
        }

        if (tipo.equals("LANZAGUISANTES")) {
            return new Lanzaguisantes(fila, columna);
        }

        if (tipo.equals("PATATAPUM")) {
            return new Patatapum(fila, columna);
        }

        if (tipo.equals("HIELAGUISANTES")) {
            return new Hielaguisantes(fila, columna);
        }

        if (tipo.equals("NUEZ")) {
            return new Nuez(fila, columna);
        }

        if (tipo.equals("CARRONIVORA")) {
            return new Carronivora(fila, columna);
        }

        if (tipo.equals("PINCHOHIERBA")) {
            return new Pinchohierba(fila, columna);
        }

        if (tipo.equals("BIPETIDORA")) {
            return new Bipetidora(fila, columna);
        }

        return null;
    }

    public Casilla getCasilla(int fila, int columna) {
        return casillas[fila][columna];
    }

    public Casilla[][] getCasillas() {
        return casillas;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
}