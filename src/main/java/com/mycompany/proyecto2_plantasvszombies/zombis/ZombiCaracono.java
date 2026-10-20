package com.mycompany.proyecto2_plantasvszombies.zombis;

public class ZombiCaracono extends Zombi {

    private int escudoCono;

    public ZombiCaracono(int fila, double posicionX) {

        super(100, 20, 100, 1.0, fila, posicionX);

        escudoCono = 100;
    }

    public int getEscudoCono() {
        return escudoCono;
    }

    public boolean tieneCono() {
        return escudoCono > 0;
    }

    @Override
    public int getDanio() { return tieneCono() ? 20 : 15; }

    @Override
    public void recibirDanio(int cantidad) {

        if (escudoCono > 0) {

            if (cantidad <= escudoCono) {

                escudoCono -= cantidad;

            } else {

                int danioRestante = cantidad - escudoCono;

                escudoCono = 0;

                super.recibirDanio(danioRestante);
            }

        } else {

            super.recibirDanio(cantidad);
        }
    }
}