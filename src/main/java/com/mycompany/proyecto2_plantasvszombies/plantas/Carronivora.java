package com.mycompany.proyecto2_plantasvszombies.plantas;

public class Carronivora extends Planta {

    private final int danioContraZombistein;
    private final int tiempoDigestion;
    private boolean digiriendo;

    public Carronivora(int fila, int columna) {

        super(100, fila, columna);

        danioContraZombistein = 200;
        tiempoDigestion = 8000;
        digiriendo = false;
    }

    public int getDanioContraZombistein() {
        return danioContraZombistein;
    }

    public int getTiempoDigestion() {
        return tiempoDigestion;
    }

    public boolean estaDigiriendo() {
        return digiriendo;
    }

    public void iniciarDigestion() {
        digiriendo = true;
    }

    public void terminarDigestion() {
        digiriendo = false;
    }
}