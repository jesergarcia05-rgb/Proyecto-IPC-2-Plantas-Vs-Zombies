package com.mycompany.proyecto2_plantasvszombies.zombis;

public class ZombiGarrocha extends Zombi {

    private boolean tieneGarrocha;
    private boolean saltoRealizado;

    public ZombiGarrocha(int fila, double posicionX) {

        super(120, 15, 100, 1.7, fila, posicionX);

        tieneGarrocha = true;
        saltoRealizado = false;
    }

    public boolean tieneGarrocha() {
        return tieneGarrocha;
    }

    public boolean haSaltado() {
        return saltoRealizado;
    }

    public void realizarSalto() {

        if (!saltoRealizado) {

            saltoRealizado = true;
            tieneGarrocha = false;

            setModificadorVelocidad(1.0);
        }
    }
}
