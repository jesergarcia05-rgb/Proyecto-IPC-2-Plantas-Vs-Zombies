package com.mycompany.proyecto2_plantasvszombies.zombis;

public class ZombiDeportista extends Zombi {

    private int escudoCasco;
    private int intervaloAtaque;

    public ZombiDeportista(int fila, double posicionX) {

        super(100, 10, 150, 2.0, fila, posicionX);

        escudoCasco = 150;
        intervaloAtaque = 500;
    }

    public int getEscudoCasco() {
        return escudoCasco;
    }

    public int getIntervaloAtaque() {
        return intervaloAtaque;
    }

    public boolean tieneCasco() {
        return escudoCasco > 0;
    }

    @Override
    public void recibirDanio(int cantidad) {

        if (escudoCasco > 0) {

            if (cantidad <= escudoCasco) {

                escudoCasco -= cantidad;

            } else {

                int danioRestante = cantidad - escudoCasco;

                escudoCasco = 0;

                super.recibirDanio(danioRestante);
            }

        } else {

            super.recibirDanio(cantidad);
        }
    }
}