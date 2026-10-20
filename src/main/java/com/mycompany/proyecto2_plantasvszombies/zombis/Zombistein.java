package com.mycompany.proyecto2_plantasvszombies.zombis;

public class Zombistein extends Zombi {

    private boolean zombiditoLanzado;

    public Zombistein(int fila, double posicionX) {

        super(300, 0, 350, 0.7, fila, posicionX);

        zombiditoLanzado = false;
    }

    public boolean haLanzadoZombidito() {
        return zombiditoLanzado;
    }

    public boolean debeLanzarZombidito() {

        return getSalud() <= getSaludMaxima() / 2
                && !zombiditoLanzado;
    }

    public void marcarZombiditoComoLanzado() {
        zombiditoLanzado = true;
    }
}