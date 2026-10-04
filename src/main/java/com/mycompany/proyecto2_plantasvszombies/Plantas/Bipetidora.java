package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Bipetidora extends Planta {

    private int danio;
    private int cadenciaFrontal;
    private int cadenciaTrasera;
    private int disparosTraseros;

    public Bipetidora(int fila, int columna) {

        super(100, fila, columna);

        danio = 15;
        cadenciaFrontal = 1000;
        cadenciaTrasera = 1000;
        disparosTraseros = 2;
    }

    public int getDanio() {
        return danio;
    }

    public int getCadenciaFrontal() {
        return cadenciaFrontal;
    }

    public int getCadenciaTrasera() {
        return cadenciaTrasera;
    }

    public int getDisparosTraseros() {
        return disparosTraseros;
    }
}