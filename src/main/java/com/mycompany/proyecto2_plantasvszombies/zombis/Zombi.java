package com.mycompany.proyecto2_plantasvszombies.zombis;

public abstract class Zombi implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private int salud;
    private int saludMaxima;
    private int danio;
    private int costo;
    private double modificadorVelocidad;

    private int fila;
    private double posicionX;

    private boolean atacando;

    public Zombi(int salud,
                 int danio,
                 int costo,
                 double modificadorVelocidad,
                 int fila,
                 double posicionX) {

        this.salud = salud;
        this.saludMaxima = salud;
        this.danio = danio;
        this.costo = costo;
        this.modificadorVelocidad = modificadorVelocidad;

        this.fila = fila;
        this.posicionX = posicionX;

        this.atacando = false;
    }

    public int getSalud() {
        return salud;
    }

    public int getSaludMaxima() {
        return saludMaxima;
    }

    public int getDanio() {
        return danio;
    }

    public int getCosto() {
        return costo;
    }

    public double getModificadorVelocidad() {
        return modificadorVelocidad;
    }

    public int getFila() {
        return fila;
    }

    public double getPosicionX() {
        return posicionX;
    }

    public boolean estaAtacando() {
        return atacando;
    }

    public void recibirDanio(int cantidad) {

        salud -= cantidad;

        if (salud < 0) {
            salud = 0;
        }
    }

    public boolean estaVivo() {
        return salud > 0;
    }

    public boolean perdioBrazo() {
        return salud <= saludMaxima / 2;
    }

    public void iniciarAtaque() {
        atacando = true;
    }

    public void detenerAtaque() {
        atacando = false;
    }

    public void mover(double distancia) {

        if (!atacando && estaVivo()) {

            posicionX += distancia * modificadorVelocidad;
        }
    }

    protected void setModificadorVelocidad(double modificadorVelocidad) {
        this.modificadorVelocidad = modificadorVelocidad;
    }

    protected void setPosicionX(double posicionX) {
        this.posicionX = posicionX;
    }

    protected void setFila(int fila) {
        this.fila = fila;
    }
}